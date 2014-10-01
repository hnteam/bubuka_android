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
import ru.espepe.bubuka.player.service.PlayerService;

/**
 * Created by wolong on 12/08/14.
 */
public class MusicPlayerFragment extends Fragment implements PlayerService.PlayerListener {
    public static MusicPlayerFragment newInstance() {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public void onPlayClick() {
        PlayerService.getInstance().onPauseMusic();
    }

    @OnClick(R.id.player_music_button_prev)
    public void onPrev() {
        PlayerService.getInstance().onMusicPrev();
    }

    @OnClick(R.id.player_music_button_next)
    public void onNext() {
        PlayerService.getInstance().onMusicNext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_music, null);
        ButterKnife.inject(this, view);
        setupUi();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupUi() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    PlayerService.getInstance().onMusicSeek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if(PlayerService.getInstance().isMusicPlaying()) {
            playButton.setImageResource(R.drawable.pause_button);
        } else {
            playButton.setImageResource(R.drawable.play_button);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PlayerService.getInstance().setMusicPlayerListener(this);
    }

    @Override
    public void onDetach() {
        PlayerService.getInstance().removeMusicPlayerListener(this);
        super.onDetach();
    }

    /*
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
    */


    public String getCurrentTrackInfo() {
        /*
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            return trackList.current().getTitle();
        }
        */

        return null;
    }

    @Override
    public void onTrack(String name, String subname) {
        if(name == null) {
            name = "unknown";
        }

        if(subname == null) {
            subname = "unknown";
        }

        if(titleView != null && artistView != null) {
            titleView.setText(name);
            artistView.setText(subname);
        }
    }

    @Override
    public void onProgress(int duration, int position) {
        if(seekBar == null || timeRightView == null || timeLeftView == null) {
            return;
        }

        final int remaining = duration - position;

        seekBar.setMax(duration);
        seekBar.setProgress(position);

        int progressSeconds = position / 1000;
        int remainingSeconds = remaining / 1000;

        int progressMinuts = progressSeconds / 60;
        progressSeconds = progressSeconds % 60;

        int remainingMinuts = remainingSeconds / 60;
        remainingSeconds = remainingSeconds % 60;

        timeLeftView.setText(String.format("%02d:%02d", progressMinuts, progressSeconds));
        timeRightView.setText(String.format("%02d:%02d", remainingMinuts, remainingSeconds));
    }

    @Override
    public void onMediaPlay() {
        if(playButton != null) {
            playButton.setImageResource(R.drawable.pause_button);
        }
    }

    @Override
    public void onMediaPause() {
        if(playButton != null) {
            playButton.setImageResource(R.drawable.play_button);
        }
    }

}
