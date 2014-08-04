package ru.espepe.bubuka.player.pojo;

import java.io.File;

/**
 * Created by wolong on 29/07/14.
 */
public class SyncConfig {
    private File syncDirectory;
    private String domain;
    private String objectCode;

    public SyncConfig(File syncDirectory, String domain, String objectCode) {
        this.syncDirectory = syncDirectory;
        this.domain = domain;
        this.objectCode = objectCode;
    }

    public File getSyncDirectory() {
        return syncDirectory;
    }

    public String getDomain() {
        return domain;
    }

    public String getObjectCode() {
        return objectCode;
    }
}
