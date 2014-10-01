package ru.espepe.bubuka.player.service;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.dao.Block;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.dao.Play;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.Timelist;
import ru.espepe.bubuka.player.dao.TimelistDao;
import ru.espepe.bubuka.player.dao.Track;
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

    private Handler handler = new Handler(Looper.getMainLooper());

    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private SurfaceHolder display;

    private Track currentMusicTrack;
    private Track currentVideoTrack;

    private PlayerListener musicPlayerListener;
    private PlayerListener videoPlayerListener;

    public void setMusicPlayerListener(PlayerListener playerListener) {
        this.musicPlayerListener = playerListener;
        if(musicPlayer != null) {
            String name = currentMusicTrack.getStorageFile().getName();
            String author = null; //currentMusicTrack.getStorageFile().getAuthor();
            this.musicPlayerListener.onTrack(name, author);
        }
    }

    public void removeMusicPlayerListener(PlayerListener playerListener) {
        if(this.musicPlayerListener == playerListener) {
            this.musicPlayerListener = null;
        }
    }

    public void setVideoPlayerListener(PlayerListener playerListener) {
        this.videoPlayerListener = playerListener;
        if(videoPlayer != null) {
            String name = currentVideoTrack.getStorageFile().getName();
            String author = null; //currentMusicTrack.getStorageFile().getAuthor();
            this.videoPlayerListener.onTrack(name, author);
        }
    }

    public void removeVideoPlayerListener(PlayerListener playerListener) {
        if(this.videoPlayerListener == playerListener) {
            this.videoPlayerListener = null;
        }
    }

    public void setDisplay(SurfaceHolder holder) {
        this.display = holder;
        if(videoPlayer != null) {
            videoPlayer.setDisplay(holder);
        }
    }

    public void removeDisplay(SurfaceHolder holder) {
        if(display == holder) {
            if(videoPlayer != null) {
                videoPlayer.setDisplay(null);
            }
            display = null;
        }
    }

    private MediaPlayer getNewVideoPlayer() {
        if(videoPlayer != null) {
            videoPlayer.reset();
            videoPlayer.release();
        }

        videoPlayer = new MediaPlayer();
        if(display != null) {
            videoPlayer.setDisplay(display);
        }

        return videoPlayer;
    }

    private MediaPlayer getNewMusicPlayer() {
        if(musicPlayer != null) {
            musicPlayer.reset();
            musicPlayer.release();
        }

        musicPlayer = new MediaPlayer();

        return musicPlayer;
    }

    private void onComplete() {
        start();
    }

    public boolean startVideo(PlayableTrack track) {
        try {
            MediaPlayer videoPlayer = getNewVideoPlayer();
            videoPlayer.setDataSource(BubukaApplication.getInstance(), track.getUri());
            videoPlayer.prepare();
            videoPlayer.start();

            final Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (PlayerService.this.videoPlayer != null) {
                            if(videoPlayerListener != null) {
                                int duration = PlayerService.this.videoPlayer.getDuration();
                                int position = PlayerService.this.videoPlayer.getCurrentPosition();
                                videoPlayerListener.onProgress(duration, position);
                            }
                            handler.postDelayed(this, 1000); // every sec update progress
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            handler.postDelayed(progressRunnable, 100); // first 100ms delay

            videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    PlayerService.this.videoPlayer = null;
                    handler.removeCallbacks(progressRunnable);
                    onComplete();
                }
            });

            if(videoPlayerListener != null) {
                videoPlayerListener.onMediaPlay();
            }

            return true;
        } catch (IOException e) {
            logger.warn("failed to open video", e);
            return false;
        }
    }

    public boolean startMusic(PlayableTrack track) {
        try {
            MediaPlayer musicPlayer = getNewMusicPlayer();
            musicPlayer.setDataSource(BubukaApplication.getInstance(), track.getUri());
            musicPlayer.prepare();
            musicPlayer.start();

            final Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (PlayerService.this.musicPlayer != null) {
                            if(musicPlayerListener != null) {
                                int duration = PlayerService.this.musicPlayer.getDuration();
                                int position = PlayerService.this.musicPlayer.getCurrentPosition();
                                musicPlayerListener.onProgress(duration, position);
                            }
                            handler.postDelayed(this, 1000); // every sec update progress
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            handler.postDelayed(progressRunnable, 100); // first 100ms delay

            musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    PlayerService.this.musicPlayer = null;
                    handler.removeCallbacks(progressRunnable);
                    onComplete();
                }
            });

            if(musicPlayerListener != null) {
                musicPlayerListener.onMediaPlay();
            }

            return true;
        } catch (IOException e) {
            logger.warn("failed to open music", e);
            return false;
        }
    }

    public void startIfNeeded() {
        if(musicPlayer == null && videoPlayer == null) {
            start();
        }
    }

    public List<Track> filterActiveTracks(List<Track> tracks) {
        List<Track> activeTracks = new ArrayList<Track>(tracks.size());
        for(Track track : tracks) {
            if(track.getStorageFile().getStatus().equals("active")) {
                activeTracks.add(track);
            }
        }
        return activeTracks;
    }

    public void start() {
        List<Play> selectedPlays = selectPlays();
        for(Play play : selectedPlays) {
            Block block = play.getBlock();
            List<Track> activeTracks = filterActiveTracks(block.getTrackList());
            if(!activeTracks.isEmpty()) {
                Collections.shuffle(activeTracks);
                for (Track track : activeTracks) {
                    startTrack(track);
                    return;
                }
            }
        }
    }

    private void startTrack(Track track) {
        final StorageFile storageFile = track.getStorageFile();
        if(storageFile.getType().equals("clip")) {
            if(storageFile.getPath().endsWith(".mp3")) {
                this.currentMusicTrack = track;
                if(musicPlayerListener != null) {
                    musicPlayerListener.onTrack(storageFile.getName(), null);
                }
                startPlayMusic(storageFile);
            } else if(storageFile.getPath().endsWith(".mp4")) {
                this.currentVideoTrack = track;
                if(videoPlayerListener != null) {
                    videoPlayerListener.onTrack(storageFile.getName(), null);
                }
                startPlayVideo(storageFile);
            }
        } else if(storageFile.getType().equals("music")) {
            this.currentMusicTrack = track;
            if(musicPlayerListener != null) {
                musicPlayerListener.onTrack(storageFile.getName(), null);
            }
            startPlayMusic(storageFile);
        } else if(storageFile.getType().equals("video")) {
            this.currentVideoTrack = track;
            if(videoPlayerListener != null) {
                videoPlayerListener.onTrack(storageFile.getName(), null);
            }
            startPlayVideo(storageFile);
        } else if(storageFile.getType().equals("photo")) {
            // TODO: start play photo
        }
    }

    private void startPlayMusic(StorageFile storageFile) {
        PlayableTrack playableTrack = PlayableTrack.from(storageFile);
        startMusic(playableTrack);
    }

    private void startPlayVideo(StorageFile storageFile) {
        PlayableTrack playableTrack = PlayableTrack.from(storageFile);
        startVideo(playableTrack);
    }

    private List<Play> selectPlays() {
        final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        final int minuts = calendar.get(Calendar.MINUTE);
        final int time = hours * 60 + minuts;

        final DaoSession daoSession = BubukaApplication.getInstance().getDaoSession();
        final TimelistDao timelistDao = daoSession.getTimelistDao();
        final List<Timelist> timelists = timelistDao.queryBuilder().orderDesc(TimelistDao.Properties.Priority).list();
        final List<Play> selectedPlays = new ArrayList<Play>();

        for(Timelist timelist : timelists) {
            final List<Play> plays = timelist.getPlayList();
            Play choosenPlay = null;

            for(Play play : plays) {
                if(play.getTime() < time) {
                    if(choosenPlay == null || play.getTime() > choosenPlay.getTime()) {
                        choosenPlay = play;
                    }
                }
            }

            if(choosenPlay != null) {
                selectedPlays.add(choosenPlay);
            }
        }

        return selectedPlays;
    }

    public void onPauseMusic() {
        try {
            if(musicPlayer != null) {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pause();
                    if(musicPlayerListener != null) {
                        musicPlayerListener.onMediaPause();
                    }
                } else {
                    musicPlayer.start();
                    if(musicPlayerListener != null) {
                        musicPlayerListener.onMediaPlay();
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("failed to pause music", e);
        }
    }

    public void onPauseVideo() {
        try {
            if(videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.pause();
                    if(videoPlayerListener != null) {
                        videoPlayerListener.onMediaPause();
                    }
                } else {
                    videoPlayer.start();
                    if(videoPlayerListener != null) {
                        videoPlayerListener.onMediaPlay();
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("failed to pause video");
        }
    }

    public void onMusicSeek(int progress) {
        if(musicPlayer != null) {
            try {
                musicPlayer.seekTo(progress);
            } catch (Exception e) {
                logger.warn("failed to seek music", e);
            }
        }
    }

    public void onVideoSeek(int progress) {
        if(videoPlayer != null) {
            try {
                videoPlayer.seekTo(progress);
            } catch (Exception e) {
                logger.warn("failed to seek video", e);
            }
        }
    }

    public void onMusicNext() {

    }

    public void onMusicPrev() {

    }

    public void onVideoNext() {

    }

    public void onVideoPrev() {

    }

    private void makePlaylists() {
        BubukaApplication.getInstance().getDaoSession().getTimelistDao();
    }

    public void onUpdateDatabase() {

    }

    public boolean isVideoPlaying() {
        try {
            return videoPlayer != null && videoPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMusicPlaying() {
        try {
            return musicPlayer != null && musicPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public static interface PlayerListener {
        void onTrack(String name, String subname);
        void onProgress(int duration, int position);
        void onMediaPlay();
        void onMediaPause();
    }
}
