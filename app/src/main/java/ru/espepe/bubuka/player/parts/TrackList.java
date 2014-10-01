package ru.espepe.bubuka.player.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.espepe.bubuka.player.dao.StorageFile;

/**
 * Created by wolong on 18/08/14.
 */
public class TrackList {
    public static TrackList from(List<StorageFile> storageFileList) {
        TrackList self = new TrackList();
        self.tracks = new ArrayList<PlayableTrack>(storageFileList.size());
        for(StorageFile storageFile : storageFileList) {
            self.tracks.add(PlayableTrack.from(storageFile));
        }

        Collections.shuffle(self.tracks);

        self.currentTrack = 0;

        return self;
    }

    private List<PlayableTrack> tracks;
    private int currentTrack;

    public PlayableTrack current() {
        return tracks.get(currentTrack);
    }

    public boolean next() {
        currentTrack += 1;
        if(currentTrack < tracks.size()) {
            return true;
        } else {
            currentTrack = 0;
            return false;
        }
    }

    public boolean prev() {
        currentTrack -= 1;
        if(currentTrack < 0) {
            currentTrack = 0;
            return false;
        } else {
            return true;
        }
    }
}
