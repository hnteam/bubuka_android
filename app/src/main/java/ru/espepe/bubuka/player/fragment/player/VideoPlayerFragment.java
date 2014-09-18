package ru.espepe.bubuka.player.fragment.player;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.activity.FullScreenActivity;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.parts.TrackList;
import ru.espepe.bubuka.player.service.PlayerService;

/**
 * Created by wolong on 12/08/14.
 */
public class VideoPlayerFragment extends Fragment {
    private static final Logger logger = LoggerFactory.getLogger(VideoPlayerFragment.class);

    public static VideoPlayerFragment newInstance() {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private TrackList trackList;
    private ProgressTask progressTask;

    @InjectView(R.id.player_video_control_panel)
    protected RelativeLayout controlPanel;

    @InjectView(R.id.player_video_seek)
    protected SeekBar seekBar;

    @InjectView(R.id.player_video_time_left)
    protected TextView timeLeftView;

    @InjectView(R.id.player_video_time_right)
    protected TextView timeRightView;

    @InjectView(R.id.player_video_title)
    protected TextView titleView;

    @InjectView(R.id.player_video_view)
    protected SurfaceView videoView;

    @InjectView(R.id.player_video_button_play)
    protected ImageButton playButton;

    @OnClick(R.id.player_video_button_play)
    protected void play() {
        MediaPlayer videoPlayer = PlayerService.getInstance().getVideoPlayer();
        if(videoPlayer.isPlaying()) {
            videoPlayer.pause();
            playButton.setImageResource(R.drawable.video_play_button);
        } else if(trackList != null) {
            videoPlayer.start();
            playButton.setImageResource(R.drawable.video_pause_button);
            updateProgressTask();
        } else {
            List<StorageFile> files = BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder().where(StorageFileDao.Properties.Type.eq("video")).list();
            if(files.size() > 0) {
                trackList = TrackList.from(files);
                changeTrack();
                playButton.setImageResource(R.drawable.video_pause_button);
            }
        }
    }

    @OnClick(R.id.player_video_button_next)
    protected void next() {
        if(trackList != null) {
            if(trackList.next()) {
                changeTrack();
            }
        }
    }

    @OnClick(R.id.player_video_button_prev)
    protected void prev() {
        if(trackList != null) {
            if(trackList.prev()) {
                changeTrack();
            }
        }
    }

    private void changeTrack() {
        MediaPlayer videoPlayer = PlayerService.getInstance().getVideoPlayer();

        /*
        try {
            videoPlayer.stop();
        } catch (Exception e) {}

        try {
            videoPlayer.setDa(trackList.current().getUri());
        } catch (Exception e) {
            logger.warn("change track error", e);
        }
        videoView.start();
        */

        PlayerService.getInstance().startVideo(trackList.current());

        videoPlayer.setOnCompletionListener(completionListener);
        updateTrackInfo();
        updateProgressTask();
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    };

    private void updateProgressTask() {
        if(progressTask != null) {
            progressTask.cancel(true);
        }

        progressTask = new ProgressTask();
        progressTask.execute(null, null, null);
    }

    private void updateTrackInfo() {
        titleView.setText(trackList.current().getName());
        ((MainActivity)getActivity()).updatePlayer();
    }

    @OnClick(R.id.player_video_button_fullscreen)
    protected void fullscreen() {
        startActivity(new Intent(getActivity(), FullScreenActivity.class));
    }

    protected void videoClick() {
        logger.info("video click");
        if(controlPanel.getVisibility() == View.INVISIBLE) {
            controlPanel.setVisibility(View.VISIBLE);

            TranslateAnimation animation = new TranslateAnimation(0, 0, controlPanel.getMeasuredHeight(), 0);
            animation.setDuration(500);
            controlPanel.startAnimation(animation);
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, controlPanel.getMeasuredHeight());
            animation.setDuration(500);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    controlPanel.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            controlPanel.startAnimation(animation);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_video, null);
        ButterKnife.inject(this, view);
        setupUi();
        return view;
    }


    private void setupUi() {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                videoClick();
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if(fromUser) {
                        ///videoView.seekTo(progress);
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

        videoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                PlayerService.getInstance().setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                PlayerService.getInstance().setDisplay(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public String getCurrentTrackInfo() {
        /*
        if(videoView != null && videoView.isPlaying()) {
            return trackList.current().getName();
        }
        */

        return null;
    }

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (videoView != null) {
                    Thread.sleep(500);
                    publishProgress();
                }

            } catch (InterruptedException e) {

            }

            logger.info("exit from progress task");

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            return;
            /*
            if(!videoView.isPlaying()) {
                return;
            }


            final int duration = videoView.getDuration();
            final int progress = videoView.getCurrentPosition();
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
            */
        }
    }
}
