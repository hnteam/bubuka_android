package ru.espepe.bubuka.player.service.sync;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.dao.Block;
import ru.espepe.bubuka.player.dao.BlockDao;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.dao.Play;
import ru.espepe.bubuka.player.dao.PlayDao;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.dao.Timelist;
import ru.espepe.bubuka.player.dao.TimelistDao;
import ru.espepe.bubuka.player.dao.Track;
import ru.espepe.bubuka.player.dao.TrackDao;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.BlockPojo;
import ru.espepe.bubuka.player.pojo.Domain;
import ru.espepe.bubuka.player.pojo.FileObject;
import ru.espepe.bubuka.player.pojo.PlayPojo;
import ru.espepe.bubuka.player.pojo.SppConfig;
import ru.espepe.bubuka.player.pojo.SyncList;
import ru.espepe.bubuka.player.pojo.SyncListFiles;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.pojo.TimeListPojo;
import ru.espepe.bubuka.player.pojo.TrackPojo;
import ru.espepe.bubuka.player.service.BubukaApi;

/**
 * Created by wolong on 27/08/14.
 */
public class Sync implements Runnable, OnSyncFileProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(Sync.class);

    private OnSyncProgressListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Thread thread;

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

    @Override
    public void run() {
        try {
            logger.info("start sync...");

            publishProgress(new SyncProgressReport("start", Collections.EMPTY_LIST));

            syncInner();

            thread = null;
            BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.COMPLETED));
            publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));

            logger.info("sync successfully finished");
        } catch (Exception e) {
            logger.error("sync exception", e);
            thread = null;
            BubukaApplication.getInstance().setSyncStatus(new SyncStatus(SyncStatus.SyncStatusType.INTERRUPTED));
            publishProgress(new SyncProgressReport("stop", Collections.EMPTY_LIST));
        }
    }

    private void syncInner() throws Exception {
        // db access
        final DaoSession daoSession = BubukaApplication.getInstance().getDaoSession();
        final TimelistDao timelistDao = daoSession.getTimelistDao();
        final PlayDao playDao = daoSession.getPlayDao();
        final BlockDao blockDao = daoSession.getBlockDao();
        final TrackDao trackDao = daoSession.getTrackDao();
        final StorageFileDao storageFileDao = daoSession.getStorageFileDao();

        // api access
        final BubukaApi api = new BubukaApi();

        logger.info("retrive sync list...");
        final SyncList syncList = api.findSyncList();

        if(syncList.getSyncObject().getVersion() == BubukaApplication.getInstance().getPreferences().getSyncVersion()) {
            // already synced
            return;
        }

        final String[] fileTypes = new String[] { "photo", "video", "music", "clip" };

        logger.info("retrieve sync config...");
        final SppConfig sppConfig = api.syncConfig(syncList.getDomains());

        final File baseDir = BubukaApplication.getInstance().getBubukaFilesDir();

        final Map<String, SyncListFiles> syncLists = new HashMap<String, SyncListFiles>();
        final Map<String, List<Domain>> syncDomains = new HashMap<String, List<Domain>>();
        final Map<String, File> syncDirs = new HashMap<String, File>();


        for(String fileType : fileTypes) {
            logger.info("retrieve {} sync list...", fileType);
            final SyncListFiles syncFiles = api.syncFiles(syncList.getDomains(), fileType);

            syncLists.put(fileType, syncFiles);
            syncDirs.put(fileType, new File(baseDir, fileType));
        }


        logger.info("retrieving lists completed");

        BubukaApplication.getInstance().getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                logger.info("start transaction");
                timelistDao.deleteAll();
                playDao.deleteAll();
                blockDao.deleteAll();
                trackDao.deleteAll();

                logger.info("all old data deleted, modify old storage files...");

                final List<StorageFile> allFiles = storageFileDao.queryBuilder().list();
                for(StorageFile storageFile : allFiles) {
                    if(storageFile.getStatus().equals("pending")) {
                        storageFileDao.delete(storageFile);
                    } else if(storageFile.getStatus().equals("active")) {
                        storageFile.setStatus("cache");
                        storageFileDao.update(storageFile);
                    }
                }

                logger.info("storage files depreceted");

                Map<String, StorageFile> fileMap = new HashMap<String, StorageFile>();

                for(String fileType : fileTypes) {
                    logger.info("processing sync list {}...", fileType);
                    final String fileDir = "./"+fileType+"/";
                    for(FileObject fileObject : syncLists.get(fileType).getObjects()) {
                        StorageFile storageFile = storageFileDao.queryBuilder().where(
                                StorageFileDao.Properties.Type.eq(fileType),
                                StorageFileDao.Properties.Identity.eq(fileObject.getId()),
                                StorageFileDao.Properties.Version.eq(fileObject.getVersion())).unique();

                        if(storageFile == null) {
                            storageFile = new StorageFile(null, fileType, fileObject.getId(), fileObject.getName(), fileObject.getPath(), fileObject.getVersion(), "pending");
                            storageFileDao.insert(storageFile);
                        } else {
                            if(storageFile.getStatus().equals("cache")) {
                                storageFile.setStatus("active");
                            }

                            storageFileDao.update(storageFile);
                        }

                        fileMap.put(fileDir + storageFile.getPath(), storageFile);
                    }
                }

                logger.info("starting blocks processing");
                // blocks processing
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                Map<String, Block> blocks = new HashMap<String, Block>();
                for(BlockPojo blockPojo : sppConfig.getBlocks().values()) {
                    Block block = new Block(null, blockPojo.getName(), blockPojo.getMediaDir(), blockPojo.getFading(), blockPojo.getLoop());
                    blockDao.insert(block);
                    blocks.put(block.getName(), block);

                    logger.info("new block '{}' inserted", block.getName());

                    for(TrackPojo trackPojo : blockPojo.getTracks()) {
                        try {
                            Date startDate = dateFormat.parse(trackPojo.getStartDate());
                            Date endDate = dateFormat.parse(trackPojo.getEndDate());
                            endDate.setTime(endDate.getTime() + 24 * 3600);


                            if(trackPojo.getFile().equals("*")) {
                                logger.info("process all tracks within block {}...", block.getMediadir());
                                String mediaType = blockPojo.getMediaDir();
                                for(Map.Entry<String, StorageFile> storageFileEntry : fileMap.entrySet()) {
                                    if(storageFileEntry.getKey().startsWith(mediaType)) {
                                        logger.info("storage file matched: {}", storageFileEntry.getKey());
                                        Track track = new Track(null, startDate, endDate, trackPojo.getFile(), block.getId(), storageFileEntry.getValue().getId());
                                        trackDao.insert(track);
                                    }
                                }
                            } else {
                                String virtualFilePath = block.getMediadir() + "/" + trackPojo.getFile();
                                logger.info("search file: {}", virtualFilePath);
                                StorageFile storageFile = fileMap.get(virtualFilePath);
                                if(storageFile != null) {
                                    logger.info("storage file found, create track");
                                    Track track = new Track(null, startDate, endDate, trackPojo.getFile(), block.getId(), storageFile.getId());
                                    trackDao.insert(track);
                                } else {
                                    logger.warn("storage file not found: {}", virtualFilePath);
                                }
                            }

                        } catch (ParseException e) {
                            logger.warn("invalid track date: {} - {}", trackPojo.getStartDate(), trackPojo.getEndDate());
                        }
                    }
                }

                logger.info("processing timelists...");

                for(TimeListPojo timeListPojo : sppConfig.getTimeLists().values()) {
                    Timelist timelist = new Timelist(null, timeListPojo.getPriority(), timeListPojo.getName());
                    timelistDao.insert(timelist);

                    logger.info("inserted timelist: {}", timelist.getName());

                    for(PlayPojo playPojo : timeListPojo.getPlayList()) {
                        logger.info("process play object: {}", playPojo.getName());
                        Block pairedBlock = blocks.get(playPojo.getBlock());
                        if(pairedBlock == null) {
                            logger.warn("failed to find block in play: {}", playPojo.getBlock());
                            continue;
                        }

                        Play play = new Play(
                                null,
                                playPojo.getName(),
                                playPojo.getVolume(),
                                playPojo.getTimeMinuts(),
                                playPojo.getFont(),
                                playPojo.getFontnew(),
                                playPojo.getAnim(),
                                playPojo.getPeriod(),
                                timelist.getId(),
                                pairedBlock.getId());

                        playDao.insert(play);
                    }
                }
            }
        });

        final List<StorageFile> pendingFiles = storageFileDao.queryBuilder().where(StorageFileDao.Properties.Status.eq("pending")).list();
        logger.info("files in pending state: {}", pendingFiles.size());

        final String objectCode = BubukaApplication.getInstance().getObjectCode();
        for(StorageFile storageFile : pendingFiles) {
            final String type = storageFile.getType();
            final List<Domain> domains = syncLists.get(type).getDomains();
            final File syncDir = syncDirs.get(type);
            final File outputFile = new File(syncDir, storageFile.getIdentity() + "_" + storageFile.getVersion());

            final SyncFileRequest request = new SyncFileRequest(this, objectCode, type, domains, storageFile.getPath(), outputFile);
            if(request.runSync()) {
                logger.info("sync file completed successfully");
                storageFile.setStatus("active");
                storageFileDao.update(storageFile);
            } else {
                throw new Exception("failed to download file");
            }
        }
    }
}
