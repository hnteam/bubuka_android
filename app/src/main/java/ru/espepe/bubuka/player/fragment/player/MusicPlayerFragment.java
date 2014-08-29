package ru.espepe.bubuka.player.fragment.player;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.fragment.PlayerFragment;
import ru.espepe.bubuka.player.parts.TrackList;
import ru.espepe.bubuka.player.service.HttpServer;

/**
 * Created by wolong on 12/08/14.
 */
public class MusicPlayerFragment extends Fragment {
    public static MusicPlayerFragment newInstance() {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private TrackList trackList;
    private ProgressTask progressTask;



    private void updateProgressTask() {
        if(progressTask != null) {
            progressTask.cancel(true);
        }

        progressTask = new ProgressTask();
        progressTask.execute(null, null, null);
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(trackList.next()) {
                changeTrack();
            }
        }
    };


    private void updateTrackInfo() {
        artistView.setText(trackList.current().getArtist());
        titleView.setText(trackList.current().getTitle());

        ((MainActivity)getActivity()).updatePlayer();
    }

    @InjectView(R.id.player_music_artist)
    protected TextView artistView;

    @InjectView(R.id.player_music_title)
    protected TextView titleView;

    @InjectView(R.id.player_music_seek)
    protected SeekBar seekBar;

    @InjectView(R.id.player_music_time_left)
    protected TextView timeLeftView;

    @InjectView(R.id.player_music_time_right)
    protected TextView timeRightView;

    @InjectView(R.id.player_music_button_play)
    protected ImageButton playButton;


    @OnClick(R.id.player_music_button_play)
    public void onPlay() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.play_button);
        } else if(trackList != null) {
            mediaPlayer.start();
            updateProgressTask();
            playButton.setImageResource(R.drawable.pause_button);
        } else {
            List<StorageFile> files = BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder().where(StorageFileDao.Properties.Type.eq("music")).list();
            if(files.size() > 0) {
                trackList = TrackList.from(files);
                changeTrack();
                playButton.setImageResource(R.drawable.pause_button);
            }
        }
    }

    @OnClick(R.id.player_music_button_prev)
    public void onPrev() {
        if(trackList != null) {
            if(trackList.prev()) {
                changeTrack();
            }
        }
    }

    @OnClick(R.id.player_music_button_next)
    public void onNext() {
        if(trackList != null) {
            if(trackList.next()) {
                changeTrack();
            }
        }
    }

    private void changeTrack() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getActivity(), trackList.current().getUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(completionListener);
            updateTrackInfo();
            updateProgressTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_music, null);
        ButterKnife.inject(this, view);
        setupUi();
        return view;
    }

    private void setupUi() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if(fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    MediaPlayer mediaPlayer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Thread.sleep(500);
                    publishProgress();
                }
            } catch (InterruptedException e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            final int duration = mediaPlayer.getDuration();
            final int progress = mediaPlayer.getCurrentPosition();
            final int remaining = duration - progress;

            seekBar.setMax(duration);
            seekBar.setProgress(progress);

            int progressSeconds = progress / 1000;
            int remainingSeconds = remaining / 1000;

            int progressMinuts = progressSeconds / 60;
            progressSeconds = progressSeconds % 60;

            int remainingMinuts = remainingSeconds / 60;
            remainingSeconds = remainingSeconds % 60;


            timeLeftView.setText(String.format("%02d:%02d", progressMinuts, progressSeconds));
            timeRightView.setText(String.format("%02d:%02d", remainingMinuts, remainingSeconds));
        }
    }

    public String getCurrentTrackInfo() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            return trackList.current().getTitle();
        }

        return null;
    }
}
