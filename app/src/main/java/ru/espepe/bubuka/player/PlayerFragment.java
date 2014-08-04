package ru.espepe.bubuka.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.InputMismatchException;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;

public class PlayerFragment extends Fragment {
    private static final Logger logger = LoggerFactory.getLogger(PlayerFragment.class);

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_player, container, false);

        ImageView playButton = (ImageView) view.findViewById(R.id.player_play_button);
        ImageView nextButton = (ImageView) view.findViewById(R.id.player_next_button);
        ImageView prevButton = (ImageView) view.findViewById(R.id.player_previous_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlay();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNext();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePrev();
            }
        });

        return view;
    }

    private void handlePrev() {

    }

    private void handleNext() {

    }

    private Handler handler = new Handler();

    private void schedule(final int ms, final int total, final Runnable task, final Runnable atEndTask) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int totalTime = total;
                    while(totalTime > 0) {
                        Thread.sleep(ms);
                        totalTime -= ms;
                        handler.post(task);
                    }
                    handler.post(atEndTask);
                } catch (Exception e) {}
            }
        }).start();
    }

    private void delay(final int ms, final Runnable task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ms);
                    handler.post(task);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    float volume1 = 1.0f;
    float volume2 = 0.0f;

    private void handlePlay() {
        try {
            final MediaPlayer mediaPlayer1 = MediaPlayer.create(getActivity(), Uri.parse("http://127.0.0.1:47329/music/5731_1"));
            mediaPlayer1.start();

            final MediaPlayer mediaPlayer2 = MediaPlayer.create(getActivity(), Uri.parse("http://127.0.0.1:47329/music/5827_1"));
            mediaPlayer2.start();

            mediaPlayer1.setVolume(1.0f, 1.0f);
            mediaPlayer2.setVolume(0.0f, 0.0f);

            delay(20000, new Runnable() {
                @Override
                public void run() {
                    schedule(10, 3000, new Runnable() {
                        @Override
                        public void run() {
                            volume1 -= 1.0f / 300.0f;
                            volume2 += 1.0f / 300.0f;
                            if (volume1 < 0.0f) volume1 = 0.0f;
                            if (volume2 > 1.0f) volume2 = 1.0f;
                            logger.info("set volume {}, {}", volume1, volume2);
                            mediaPlayer1.setVolume(volume1, volume1);
                            mediaPlayer2.setVolume(volume2, volume2);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            });

            //mediaPlayer.release();
            Equalizer equalizer;
        } catch (Exception e) {
            logger.error("play error", e);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
