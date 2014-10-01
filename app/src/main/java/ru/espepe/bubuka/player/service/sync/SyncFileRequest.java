package ru.espepe.bubuka.player.service.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import ru.espepe.bubuka.player.helper.MainHelper;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.Domain;
import ru.espepe.bubuka.player.pojo.FileObject;

/**
 * Created by wolong on 27/08/14.
 */
public class SyncFileRequest {
    private static final Logger logger = LoggerFactory.getLogger(SyncFileRequest.class);

    private final OnSyncFileProgressListener listener;
    private final String objectCode;
    private final String type;
    private final List<Domain> domains;
    private final File outputFile;
    private final String urlPath;

    public SyncFileRequest(OnSyncFileProgressListener listener, String objectCode, String type, List<Domain> domains, String urlPath, File outputFile) {
        this.listener = listener;
        this.objectCode = objectCode;
        this.type = type;
        this.domains = domains;
        this.outputFile = outputFile;
        this.urlPath = urlPath;
    }

    public boolean isExists() {
        return outputFile.exists();
    }

    public boolean runSync() {
        logger.info("start sync file {}", outputFile.getAbsolutePath());
        File tempFile = new File(outputFile.getAbsolutePath() + ".part");

        listener.onFileProgress(new SyncFileProgressReport("start", type, 0, 0));


        for(Domain domain : domains) {
            OutputStream outputStream = null;
            OutputStream cryptoStream = null;
            try {
                URL url = new URL(domain.getUrl() + objectCode + "/" + type + "/" + urlPath);
                logger.debug("start downloading file {} to temporary path {}", url, tempFile.getAbsolutePath());
                outputStream = new FileOutputStream(tempFile);

                // TODO: enable encryption
                //cryptoStream = BubukaApplication.getInstance().getCrypto().getCipherOutputStream(outputStream, new Entity(fileObject.getPath()));
                cryptoStream = outputStream;

                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int contentLength = urlConnection.getContentLength();
                InputStream inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[1024 * 128];
                int len = inputStream.read(buffer, 0, buffer.length);
                int downloaded = 0;
                int lastChunkReported = 0;
                while(len > 0) {
                    if(Thread.currentThread().isInterrupted()) {
                        return false;
                    }

                    cryptoStream.write(buffer, 0, len);
                    downloaded += len;
                    int chunkNumber = downloaded / 100000;
                    if(chunkNumber > lastChunkReported) {
                        logger.info("downloaded {} bytes, chunk {}", MainHelper.humanReadableByteCountOld(downloaded, false), chunkNumber);
                        lastChunkReported = chunkNumber;

                        listener.onFileProgress(new SyncFileProgressReport("progress", type, contentLength, downloaded));
                    }

                    len = inputStream.read(buffer, 0, buffer.length);

                    if(Thread.currentThread().isInterrupted()) {
                        return false;
                    }
                }

                cryptoStream.close();
                cryptoStream = null;
                outputStream = null;

                logger.info("file download complete, rename {} to {}", tempFile.getAbsolutePath(), outputFile.getAbsoluteFile());

                tempFile.renameTo(outputFile);

                logger.info("sync file successfully completed");
                return true;
            } catch (Exception e) {
                logger.warn("failed to download file", e);
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {}
                }

                listener.onFileProgress(new SyncFileProgressReport("stop", type, 0, 0));
            }
        }

        return false;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public String getType() {
        return type;
    }
}
