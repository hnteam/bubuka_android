package ru.espepe.bubuka.player;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.google.gson.Gson;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.espepe.bubuka.player.dao.DaoMaster;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.pojo.PlayList;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.service.BubukaApi;
import ru.espepe.bubuka.player.service.BubukaPreferences;
import ru.espepe.bubuka.player.service.HttpServer;
import ru.espepe.bubuka.player.service.sync.OnSyncFileProgressListener;
import ru.espepe.bubuka.player.service.sync.OnSyncProgressListener;
import ru.espepe.bubuka.player.service.sync.Sync;
import ru.espepe.bubuka.player.service.sync.SyncTask;

/**
 * Created by wolong on 30/07/14.
 */
@ReportsCrashes(
        formKey = "",
        formUri = "http://naphaso.com/acra/report.php",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST, // PUT
        formUriBasicAuthLogin = "bubuka",
        formUriBasicAuthPassword = "some_password",
        mode = ReportingInteractionMode.SILENT
)
public class BubukaApplication extends Application {
    private static BubukaApplication instance;

    public static BubukaApplication getInstance() {
        return instance;
    }

    private Crypto crypto;
    private DaoMaster.DevOpenHelper dbHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private HttpServer httpServer;

    private File filesDir;

    private Sync syncTask;

    private BubukaPreferences preferences;

    @Override
    public void onCreate() {
        instance = this;

        crypto = new Crypto(
                new SharedPrefsBackedKeyChain(this),
                new SystemNativeCryptoLibrary());


        super.onCreate();

        ACRA.init(this);

        filesDir = getExternalFilesDir(null);

        syncTask = new Sync();

        httpServer = new HttpServer(filesDir);
        httpServer.start();

        dbHelper = new DaoMaster.DevOpenHelper(this, "db", null);
        daoMaster = new DaoMaster(dbHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();

        preferences = new BubukaPreferences(this);

        retrievePlaylists();
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public String getObjectCode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("object_code", null);
    }

    public void setObjectCode(String objectCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("object_code", objectCode).commit();
    }

    public File getBubukaFilesDir() {
        return filesDir;
    }

    public String getBubukaDomain() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("bubuka_domain", "bubuka.espepe.ru");
    }

    public void setBubukaDomain(String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("bubuka_domain", value).commit();
    }

    public void toggleSync() {
        if(syncTask.isRunning()) {
            syncTask.stop();
        } else {
            syncTask.start();
        }
    }

    public void removeSyncListener() {
        syncTask.setListener(null);
    }

    public void setSyncListener(OnSyncProgressListener listener) {
        syncTask.setListener(listener);
    }

    private Gson gson = new Gson();

    private SyncStatus currentSyncStatus;
    public void setSyncStatus(SyncStatus syncStatus) {
        this.currentSyncStatus = syncStatus;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("sync_status", gson.toJson(syncStatus)).commit();
    }

    public SyncStatus getSyncStatus() {
        if(currentSyncStatus == null) {
            readSyncStatus();
        }

        return currentSyncStatus;
    }

    private void readSyncStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String syncStatusString = prefs.getString("sync_status", null);

        SyncStatus syncStatus;
        if(syncStatusString == null) {
            syncStatus = new SyncStatus(SyncStatus.SyncStatusType.NOT_RUNNING);
        } else {
            try {
                syncStatus = gson.fromJson(syncStatusString, SyncStatus.class);
            } catch (Exception e) {
                syncStatus = new SyncStatus(SyncStatus.SyncStatusType.NOT_RUNNING);
            }
        }

        setSyncStatus(syncStatus);
    }


    public BubukaPreferences getPreferences() {
        return preferences;
    }

    private List<PlayList> readyPlaylists;
    private List<PlayList> musicPlaylists;
    private List<PlayList> videoPlaylists;
    private List<PlayList> slidePlaylists;
    private Map<String, PlayList> currentPlayLists = new HashMap<String, PlayList>();

    private void retrievePlaylists() {
        retrievePreparedPlaylists();
        retrievePlaylistsType("music");
        retrievePlaylistsType("video");
        retrievePlaylistsType("slide");
    }

    public void retrievePreparedPlaylists() {
        BubukaApi api = new BubukaApi(getBubukaDomain(), getObjectCode());
        api.getPreparedPlaylists(this, new BubukaApi.RetrievePlaylistsListener() {
            @Override
            public void onPlaylistsSuccess(List<PlayList> playLists) {
                synchronized (BubukaApplication.this) {
                    readyPlaylists = playLists;
                    checkAndNotifyPlaylistsWatchers();
                }
            }

            @Override
            public void onPlaylistsFailed() {

                //retrievePreparedPlaylists();
            }
        });
    }

    public void retrievePlaylistsType(final String type) {
        BubukaApi api = new BubukaApi(getBubukaDomain(), getObjectCode());
        api.getPlaylist(this, type, new BubukaApi.RetrievePlaylistsListener() {
            @Override
            public void onPlaylistsSuccess(List<PlayList> playLists) {
                synchronized (BubukaApplication.this) {
                    if (type.equals("music")) {
                        musicPlaylists = playLists;
                    } else if(type.equals("video")) {
                        videoPlaylists = playLists;
                    } else if(type.equals("slide")) {
                        slidePlaylists = playLists;
                    }

                    checkAndNotifyPlaylistsWatchers();
                }
            }

            @Override
            public void onPlaylistsFailed() {

                //retrievePlaylistsType(type);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void checkAndNotifyPlaylistsWatchers() {
        List<PlayList>[] playlistsLists = new List[] {readyPlaylists, musicPlaylists, videoPlaylists, slidePlaylists};
        if(allNotNull(playlistsLists)) {
            for(PlayList playList : readyPlaylists) {
                if(playList.isActive()) {
                    currentPlayLists.put("music", playList);
                    currentPlayLists.put("video", playList);
                    currentPlayLists.put("photo", playList);
                    return;
                }
            }

            for(PlayList playList : musicPlaylists) {
                if(playList.isActive()) {
                    currentPlayLists.put("music", playList);
                }
            }

            for(PlayList playList : videoPlaylists) {
                if(playList.isActive()) {
                    currentPlayLists.put("video", playList);
                }
            }

            for(PlayList playList : slidePlaylists) {
                if(playList.isActive()) {
                    currentPlayLists.put("photo", playList);
                }
            }
        }
    }

    private boolean allNotNull(List[] list) {
        for(Object o : list) {
            if(o == null) {
                return false;
            }
        }

        return true;
    }

    public PlayList getCurrentPlayList(String type) {
        return currentPlayLists.get(type);
    }
}
