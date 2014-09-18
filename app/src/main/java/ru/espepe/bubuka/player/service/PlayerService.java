package ru.espepe.bubuka.player.service;

import android.app.Application;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.widget.VideoView;

import java.io.IOException;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.parts.PlayableTrack;

/**
 * Created by wolong on 29/08/14.
 */
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private static final PlayerService instance = new PlayerService();

    public static PlayerService getInstance() {
        return instance;
    }

    private MediaPlayer videoPlayer = new MediaPlayer();
    private MediaPlayer musicPlayer = new MediaPlayer();


    public void setDisplay(SurfaceHolder holder) {
        videoPlayer.setDisplay(holder);
        //videoPlayer.setSurface(holder.getSurface());
    }

    public boolean startVideo(PlayableTrack track) {

        try {
            videoPlayer.setDataSource(BubukaApplication.getInstance(), track.getUri());
            videoPlayer.prepare();
            videoPlayer.start();
            return true;
        } catch (IOException e) {
            logger.warn("failed to open video", e);
            return false;
        }
    }

    public MediaPlayer getVideoPlayer() {
        return videoPlayer;
    }


}
