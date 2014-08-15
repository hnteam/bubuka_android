package ru.espepe.bubuka.player;

import android.app.Application;
import android.widget.Toast;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import ru.espepe.bubuka.player.dao.DaoMaster;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.service.HttpServer;

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

    @Override
    public void onCreate() {
        instance = this;

        crypto = new Crypto(
                new SharedPrefsBackedKeyChain(this),
                new SystemNativeCryptoLibrary());


        super.onCreate();


        httpServer = new HttpServer();
        httpServer.start();

        dbHelper = new DaoMaster.DevOpenHelper(this, "db", null);
        daoMaster = new DaoMaster(dbHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
