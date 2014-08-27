package ru.espepe.bubuka.player.service.sync;

/**
 * Created by wolong on 27/08/14.
 */
public class SyncFileProgressReport {
    public String type;
    public String fileType;
    public long bytesTotal;
    public long bytesDownloaded;

    public SyncFileProgressReport(String type, String fileType, long bytesTotal, long bytesDownloaded) {
        this.type = type;
        this.fileType = fileType;
        this.bytesTotal = bytesTotal;
        this.bytesDownloaded = bytesDownloaded;
    }
}
