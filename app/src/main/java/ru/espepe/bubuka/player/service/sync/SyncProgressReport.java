package ru.espepe.bubuka.player.service.sync;

import java.util.List;

/**
 * Created by wolong on 27/08/14.
 */
public class SyncProgressReport {
    private String type;
    //public int filesTotal;
    //public int filesComplete;
    private List<SyncFileProgressReport> filesInProgress;

    public SyncProgressReport(String type, List<SyncFileProgressReport> filesInProgress) {
        this.type = type;
        //this.filesTotal = filesTotal;
        //this.filesComplete = filesComplete;
        this.filesInProgress = filesInProgress;
    }

    public String getType() {
        return type;
    }

    public List<SyncFileProgressReport> getFilesInProgress() {
        return filesInProgress;
    }
}
