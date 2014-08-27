package ru.espepe.bubuka.player;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.google.gson.Gson;

import java.io.File;

import ru.espepe.bubuka.player.dao.DaoMaster;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.service.BubukaPreferences;
import ru.espepe.bubuka.player.service.HttpServer;
import ru.espepe.bubuka.player.service.sync.OnSyncFileProgressListener;
import ru.espepe.bubuka.player.service.sync.OnSyncProgressListener;
import ru.espepe.bubuka.player.service.sync.Sync;
import ru.espepe.bubuka.player.service.sync.SyncTask;

/**
 * Created by wolong on 30/07/14.
 */
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

        filesDir = getExternalFilesDir(null);

        syncTask = new Sync();

        httpServer = new HttpServer(filesDir);
        httpServer.start();

        dbHelper = new DaoMaster.DevOpenHelper(this, "db", null);
        daoMaster = new DaoMaster(dbHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();

        preferences = new BubukaPreferences(this);
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
        // TODO: move to properties
        return "http://bubuka.espepe.ru/users/";
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
}
