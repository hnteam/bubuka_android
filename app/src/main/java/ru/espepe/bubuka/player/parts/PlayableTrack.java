package ru.espepe.bubuka.player.parts;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.util.HashMap;

import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.service.HttpServer;

/**
 * Created by wolong on 18/08/14.
 */
public class PlayableTrack {
    private String type;
    private String filename;

    private String artist;
    private String title;
    private String name;

    public static PlayableTrack from(StorageFile storageFile) {
        PlayableTrack self = new PlayableTrack();
        self.type = storageFile.getType();
        self.filename = storageFile.getIdentity() + "_" + storageFile.getVersion();
        self.name = storageFile.getName() == null ? "unknown" : storageFile.getName();
        return self;
    }

    public String getFilename() {
        return filename;
    }

    public String getUriString() {
        return "http://127.0.0.1:" + HttpServer.SERVER_PORT + "/" + type + "/" + filename;
    }

    public Uri getUri() {
        return Uri.parse(getUriString());
    }

    private void retrieveMetainfo() {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(getUriString(), new HashMap<String, String>());
        artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(artist == null) {
            artist = "unknown";
        }

        if(title == null) {
            title = "unknown";
        }
    }

    public String getArtist() {
        if(artist == null) {
            retrieveMetainfo();
        }
        return artist;
    }

    public String getTitle() {
        if(title == null) {
            retrieveMetainfo();
        }
        return title;
    }

    public String getName() {
        return name;
    }
}
