package ru.espepe.bubuka.player.fragment;

import android.app.FragmentManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.fragment.player.FastAudioPlayerFragment;
import ru.espepe.bubuka.player.fragment.player.MusicPlayerFragment;
import ru.espepe.bubuka.player.fragment.player.VideoPlayerFragment;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;

public class PlayerFragment extends Fragment {
    private static final Logger logger = LoggerFactory.getLogger(PlayerFragment.class);
    private FragmentStatePagerAdapter adapter;

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.player_pager)
    protected ViewPager playerPager;

    @InjectView(R.id.player_pager_title_strip)
    protected PagerTabStrip pagerTitleStrip;


    private FastAudioPlayerFragment fastAudioFragment;
    private VideoPlayerFragment videoPlayerFragment;
    private MusicPlayerFragment musicPlayerFragment;

    private static class Page {
        Fragment fragment;
        String title;

        private Page(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }

    private Page[] pages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                      Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        fastAudioFragment = FastAudioPlayerFragment.newInstance();
        videoPlayerFragment = VideoPlayerFragment.newInstance();
        musicPlayerFragment = MusicPlayerFragment.newInstance();

        pages = new Page[] {
                new Page(fastAudioFragment, inflater.getContext().getString(R.string.my_fast_audio_title)),
                new Page(musicPlayerFragment, inflater.getContext().getString(R.string.music_player_title)),
                new Page(videoPlayerFragment, inflater.getContext().getString(R.string.video_player_title)),

        };

        adapter = new PagesAdapter(getFragmentManager());
        playerPager.setAdapter(adapter);

        pagerTitleStrip.setTabIndicatorColor(Color.parseColor("#e86f1c"));


        return view;
    }


    private class PagesAdapter extends FragmentStatePagerAdapter {
        private final FragmentManager fm;

        public PagesAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return pages[position].fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages[position].title;
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override @SuppressWarnings("NewApi")
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            try {
                fragment.setUserVisibleHint(true);
            } catch (Throwable e) {}
            super.destroyItem(container, position, object);
        }
    }

    public String getCurrentMusicTrackInfo() {
        return musicPlayerFragment.getCurrentTrackInfo();
    }

    public String getCurrentVideoTrackInfo() {
        return videoPlayerFragment.getCurrentTrackInfo();
    }

    /*
    @InjectView(R.id.play_prev_button)
    protected ImageView prevButton;

    @InjectView(R.id.player_next_button)
    protected ImageView nextButton;

    @InjectView(R.id.play_run_button)
    protected ImageView playButton;

    @OnClick(R.id.play_run_button)
    protected void play() {
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



    public PlayerFragment() {
        // Required empty public constructor
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
*/
}
