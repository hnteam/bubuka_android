package ru.espepe.bubuka.player.pojo;

import java.util.Date;

/**
 * Created by wolong on 26/08/14.
 */
public class SyncStatus {
    private Date date;
    private SyncStatusType type;

    public SyncStatus(SyncStatusType type) {
        this.type = type;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SyncStatusType getType() {
        return type;
    }

    public void setType(SyncStatusType type) {
        this.type = type;
    }

    public static enum SyncStatusType {
        NOT_RUNNING, INTERRUPTED, COMPLETED
    }
}
