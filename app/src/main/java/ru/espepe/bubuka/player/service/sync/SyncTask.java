package ru.espepe.bubuka.player.service.sync;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
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
import ru.espepe.bubuka.player.pojo.SyncList;
import ru.espepe.bubuka.player.pojo.SyncListFiles;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.service.BubukaApi;

/**
 * Created by wolong on 28/07/14.
 */
@Deprecated
public class SyncTask implements Runnable, OnSyncFileProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(SyncTask.class);

    private static final String DIR_NAME_PHOTO = "photo";
    private static final String DIR_NAME_MUSIC = "music";
    private static final String DIR_NAME_VIDEO = "video";


    private OnSyncProgressListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Thread thread;

    public SyncTask() {

    }

    public OnSyncProgressListener getListener() {
        return listener;
    }

    public void setListener(OnSyncProgressListener listener) {
        this.listener = listener;
    }

    public void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.setName("SyncThread");
            thread.start();
        }
    }

    public void stop() {
        if(thread != null) {
            if(!thread.isInterrupted()) {
                thread.interrupt();
            }

            thread = null;
        }
    }

    public boolean isRunning() {
        return thread != null && !thread.isInterrupted();
    }

    @Override
    public void run() {
        try {
            logger.info("start sync...");

            publishProgress(new SyncProgressReport("start", Collections.EMPTY_LIST));

            String domain = BubukaApplication.getInstance().getBubukaDomain();
            String objectCode = BubukaApplication.getInstance().getObjectCode();
            BubukaApi api = new BubukaApi(domain, objectCode);

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
            File syncDirBase = BubukaApplication.getInstance().getBubukaFilesDir();
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
                final File outputFile = new File(syncDirMusic, fileObject.getId() + "_" + fileObject.getVersion());
                syncFiles.add(outputFile);
                SyncFileRequest syncRequest = new SyncFileRequest(this, objectCode, "music", musicSyncList.getDomains(), fileObject.getPath(), outputFile);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }

            for(FileObject fileObject : videoSyncList.getObjects()) {
                final File outputFile = new File(syncDirMusic, fileObject.getId() + "_" + fileObject.getVersion());
                syncFiles.add(outputFile);
                SyncFileRequest syncRequest = new SyncFileRequest(this, objectCode, "video", musicSyncList.getDomains(), fileObject.getPath(), outputFile);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }

            for(FileObject fileObject : photoSyncList.getObjects()) {
                final File outputFile = new File(syncDirMusic, fileObject.getId() + "_" + fileObject.getVersion());
                syncFiles.add(outputFile);
                SyncFileRequest syncRequest = new SyncFileRequest(this, objectCode, "photo", musicSyncList.getDomains(), fileObject.getPath(), outputFile);
                if(!syncRequest.isExists()) {
                    syncRequests.add(syncRequest);
                }
            }


            Collections.shuffle(syncRequests);

            logger.info("total sync files {}, need download {}", syncFiles.size(), syncRequests.size());

            File[] photoFilesExists = syncDirPhoto.listFiles();
            File[] videoFilesExists = syncDirVideo.listFiles();
            File[] musicFilesExists = syncDirMusic.listFiles();

            StorageFileDao storageFileDao = BubukaApplication.getInstance().getDaoSession().getStorageFileDao();
            List<StorageFile> storageFiles = storageFileDao.queryBuilder().build().forCurrentThread().list();

            logger.info("total files exists: photo {}, video {}, music {}", photoFilesExists.length, videoFilesExists.length, musicFilesExists.length);

            for(StorageFile storageFile : storageFiles) {
                File file = getFileForStorageFile(storageFile);
                if(!file.exists()) {
                    storageFileDao.deleteInTx(storageFile);
                }
            }

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

            for(int i = 0; i < syncRequests.size(); i++) {
                if(Thread.currentThread().isInterrupted()) {
                    publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));
                    BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.INTERRUPTED));
                    return;
                }

                SyncFileRequest request = syncRequests.get(i);

                if(request.runSync()) {
                    /* // TODO: temporary remove
                    FileObject fileObject = request.getFileObject();

                    storageFileDao.insertInTx(new StorageFile(
                            null,
                            request.getType(),
                            fileObject.getId(),
                            fileObject.getName(),
                            fileObject.getPath(),
                            fileObject.getVersion(),
                            "active"
                    ));
                    */
                } else {
                    publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));
                    BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.INTERRUPTED));
                    return;
                }
            }

            publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));
            BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.COMPLETED));
        } catch (Exception e) {
            logger.error("failed to sync", e);
            publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));
            BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.INTERRUPTED));
        }
    }

    private File getFileForStorageFile(StorageFile storageFile) {
        return new File(new File(BubukaApplication.getInstance().getFilesDir(), storageFile.getType()), storageFile.getId() + "_" + storageFile.getVersion());
    }

    private void publishProgress(final SyncProgressReport progressReport) {
        if(listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onProgress(progressReport);
                }
            });
        }
    }


    @Override
    public void onFileProgress(SyncFileProgressReport report) {
        publishProgress(new SyncProgressReport("progress", Collections.singletonList(report)));
    }
}
