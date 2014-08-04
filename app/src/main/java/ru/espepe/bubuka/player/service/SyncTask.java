package ru.espepe.bubuka.player.service;

import android.os.AsyncTask;

import com.facebook.crypto.Entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.helper.MainHelper;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.Domain;
import ru.espepe.bubuka.player.pojo.FileObject;
import ru.espepe.bubuka.player.pojo.SppConfig;
import ru.espepe.bubuka.player.pojo.SyncConfig;
import ru.espepe.bubuka.player.pojo.SyncList;
import ru.espepe.bubuka.player.pojo.SyncListFiles;

/**
 * Created by wolong on 28/07/14.
 */
public class SyncTask extends AsyncTask<SyncConfig, SyncTask.SyncProgressReport, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(SyncTask.class);

    private static final String DIR_NAME_PHOTO = "photo";
    private static final String DIR_NAME_MUSIC = "music";
    private static final String DIR_NAME_VIDEO = "video";

    @Override
    protected Boolean doInBackground(SyncConfig... params) {
        try {
            logger.info("start sync...");
            SyncConfig syncConfig = params[0];

            BubukaApi api = new BubukaApi(syncConfig.getDomain(), syncConfig.getObjectCode());

            logger.info("retrive sync list...");
            SyncList syncList = api.findSyncList();
            logger.info("retrieve sync config...");
            SppConfig sppConfig = api.syncConfig(syncList.getDomains());
            logger.info("retrieve photo sync list...");
            SyncListFiles photoSyncList = api.syncFiles(syncList.getDomains(), "photo");
            logger.info("retrieve video sync list...");
            SyncListFiles videoSyncList = api.syncFiles(syncList.getDomains(), "video");
            logger.info("retrieve music sync list...");
            SyncListFiles musicSyncList = api.syncFiles(syncList.getDomains(), "music");

            logger.info("checking directory availability...");
            File syncDirBase = syncConfig.getSyncDirectory();
            File syncDirPhoto = new File(syncDirBase, DIR_NAME_PHOTO);
            File syncDirVideo = new File(syncDirBase, DIR_NAME_VIDEO);
            File syncDirMusic = new File(syncDirBase, DIR_NAME_MUSIC);

            if(!syncDirBase.exists()) {
                syncDirBase.mkdirs();
            }

            if(!syncDirPhoto.exists()) {
                syncDirPhoto.mkdirs();
            }

            if(!syncDirVideo.exists()) {
                syncDirVideo.mkdirs();
            }

            if(!syncDirMusic.exists()) {
                syncDirMusic.mkdirs();
            }

            logger.info("performing sync file lists, check cache existing and compose download list");
            List<SyncFileRequest> syncRequests = new ArrayList<SyncFileRequest>();
            Set<File> syncFiles = new HashSet<File>();

            for(FileObject fileObject : musicSyncList.getObjects()) {
                syncFiles.add(new File(syncDirMusic, fileObject.getId() + "_" + fileObject.getVersion()));
                SyncFileRequest syncRequest = new SyncFileRequest(syncConfig.getObjectCode(), "music", musicSyncList.getDomains(), syncDirMusic, fileObject);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }

            for(FileObject fileObject : photoSyncList.getObjects()) {
                syncFiles.add(new File(syncDirPhoto, fileObject.getId() + "_" + fileObject.getVersion()));
                SyncFileRequest syncRequest = new SyncFileRequest(syncConfig.getObjectCode(), "photo", photoSyncList.getDomains(), syncDirPhoto, fileObject);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }

            for(FileObject fileObject : videoSyncList.getObjects()) {
                syncFiles.add(new File(syncDirVideo, fileObject.getId() + "_" + fileObject.getVersion()));
                SyncFileRequest syncRequest = new SyncFileRequest(syncConfig.getObjectCode(), "video", videoSyncList.getDomains(), syncDirVideo, fileObject);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }


            logger.info("total sync files {}, need download {}", syncFiles.size(), syncRequests.size());

            File[] photoFilesExists = syncDirPhoto.listFiles();
            File[] videoFilesExists = syncDirVideo.listFiles();
            File[] musicFilesExists = syncDirMusic.listFiles();

            logger.info("total files exists: photo {}, video {}, music {}", photoFilesExists.length, videoFilesExists.length, musicFilesExists.length);

            for(File currentFile : photoFilesExists) {
                if(!syncFiles.contains(currentFile)) {
                    logger.info("file {} not in sync list, delete it", currentFile.getAbsolutePath());
                    currentFile.delete();
                }
            }

            for(File currentFile : videoFilesExists) {
                if(!syncFiles.contains(currentFile)) {
                    logger.info("file {} not in sync list, delete it", currentFile.getAbsolutePath());
                    currentFile.delete();
                }
            }

            for(File currentFile : musicFilesExists) {
                if(!syncFiles.contains(currentFile)) {
                    logger.info("file {} not in sync list, delete it", currentFile.getAbsolutePath());
                    currentFile.delete();
                }
            }

            logger.info("start downloading {} files", syncRequests.size());

            StorageFileDao storageFileDao = BubukaApplication.getInstance().getDaoSession().getStorageFileDao();
            for(int i = 0; i < syncRequests.size(); i++) {
                publishProgress(new SyncProgressReport(syncRequests.size(), i, syncRequests.get(i).fileObject.getPath()));
                if(syncRequests.get(i).runSync()) {
                    storageFileDao.insert(new StorageFile(null, syncRequests.get(i).type, syncRequests.get(i).fileObject.getId(), syncRequests.get(i).fileObject.getName(), syncRequests.get(i).fileObject.getPath(), syncRequests.get(i).fileObject.getVersion()));
                }
            }

            logger.info("debug interrupt, not implemented :(");
            return true;
        } catch (Exception e) {
            logger.error("failed to sync", e);
            return false;
        }
    }

    public static class SyncProgressReport {
        public int filesTotal;
        public int filesComplete;
        public String currentFile;

        public SyncProgressReport(int filesTotal, int filesComplete, String currentFile) {
            this.filesTotal = filesTotal;
            this.filesComplete = filesComplete;
            this.currentFile = currentFile;
        }
    }

    private static class SyncFileRequest {
        private final String objectCode;
        private final String type;
        private final List<Domain> domains;
        private final File syncDir;
        private final FileObject fileObject;
        private final File outputFile;

        public SyncFileRequest(String objectCode, String type, List<Domain> domains, File syncDir, FileObject fileObject) {
            this.objectCode = objectCode;
            this.type = type;
            this.domains = domains;
            this.syncDir = syncDir;
            this.fileObject = fileObject;
            this.outputFile = new File(syncDir, fileObject.getId() + "_" + fileObject.getVersion());
        }

        public boolean isExists() {
            return outputFile.exists();
        }

        public boolean runSync() {
            logger.info("start sync file {}", outputFile.getAbsolutePath());
            File tempFile = new File(outputFile.getAbsolutePath() + ".part");

            for(Domain domain : domains) {
                OutputStream outputStream = null;
                OutputStream cryptoStream = null;
                try {
                    URL url = new URL(domain.getUrl() + objectCode + "/" + type + "/" + fileObject.getPath());
                    logger.debug("start downloading file {} to temporary path {}", url, tempFile.getAbsolutePath());
                    outputStream = new FileOutputStream(tempFile);

                    // TODO: enable encryption
                    //cryptoStream = BubukaApplication.getInstance().getCrypto().getCipherOutputStream(outputStream, new Entity(fileObject.getPath()));
                    cryptoStream = outputStream;

                    InputStream inputStream = url.openStream();

                    byte[] buffer = new byte[4096];
                    int len = inputStream.read(buffer, 0, buffer.length);
                    int downloaded = 0;
                    int lastChunkReported = 0;
                    while(len > 0) {
                        cryptoStream.write(buffer, 0, len);
                        downloaded += len;
                        if(downloaded % 100000 > lastChunkReported) {
                            logger.info("downloaded {} bytes", MainHelper.humanReadableByteCountOld(downloaded, false));
                            lastChunkReported = downloaded % 100000;
                        }
                        len = inputStream.read(buffer, 0, buffer.length);
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
                }
            }

            return false;
        }
    }
}
