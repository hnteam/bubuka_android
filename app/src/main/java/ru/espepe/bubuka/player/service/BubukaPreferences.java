package ru.espepe.bubuka.player.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.espepe.bubuka.player.BubukaApplication;

/**
 * Created by wolong on 27/08/14.
 */
public final class BubukaPreferences {
    private final BubukaApplication application;

    public BubukaPreferences(BubukaApplication application) {
        this.application = application;
    }

    public int getSyncVersion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        return prefs.getInt("sync_version", -1);
    }

    public void setSyncVersion(int version) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        prefs.edit().putInt("sync_version", version).commit();
    }

    public int getRefreshPeriod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        return prefs.getInt("refresh_period", 60);
    }

    public void setRefreshPeriod(int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        prefs.edit().putInt("refresh_period", value).apply();
    }


}
