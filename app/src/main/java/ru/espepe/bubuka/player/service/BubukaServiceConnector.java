package ru.espepe.bubuka.player.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import ru.espepe.bubuka.player.IBubukaService;

/**
 * Created by wolong on 28/07/14.
 */
public class BubukaServiceConnector {
    private Activity activity;

    private IBubukaService bubukaService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bubukaService = IBubukaService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bubukaService = null;
        }
    };

    public void onCreate(Activity activity) {
        this.activity = activity;
        this.activity.startService(new Intent(this.activity, BubukaService.class));
        this.activity.bindService(new Intent(this.activity, BubukaService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onDestroy() {
        this.activity.unbindService(serviceConnection);
    }

    public void test() {
        if(bubukaService != null) {
            try {
                bubukaService.basicTypes(123, 123, true, 123.0f, 123.0f, "some string");
            } catch (RemoteException e) {

            }
        }
    }
}
