package ru.espepe.bubuka.player;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import ru.espepe.bubuka.player.activity.FullScreenActivity;
import ru.espepe.bubuka.player.adapter.NavigationAdapter;
import ru.espepe.bubuka.player.fragment.MainFragment;
import ru.espepe.bubuka.player.fragment.NavigationFragment;
import ru.espepe.bubuka.player.fragment.PlayerFragment;
import ru.espepe.bubuka.player.fragment.screen.AboutScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.MainScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.PlaylistsScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.SettingsScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.TimeTableScreenFragment;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.service.sync.OnSyncProgressListener;
import ru.espepe.bubuka.player.service.sync.SyncProgressReport;
import ru.espepe.bubuka.player.service.sync.SyncTask;


public class MainActivity extends Activity implements NavigationAdapter.OnMenuItemListener, OnSyncProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private NavigationFragment navigationFragment;

    // screen fragments
    private MainScreenFragment mainScreenFragment;
    private PlaylistsScreenFragment playlistsScreenFragment;
    private TimeTableScreenFragment timeTableScreenFragment;
    private AboutScreenFragment aboutScreenFragment;
    private SettingsScreenFragment settingsScreenFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(BubukaApplication.getInstance().getObjectCode() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e86f1c")));
        ButterKnife.inject(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        if(drawerLayout != null) {
            navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.main_navigation_fragment);
            navigationFragment.setUp(R.id.main_navigation_fragment, drawerLayout);
        }


        mainScreenFragment = new MainScreenFragment();
        playlistsScreenFragment = new PlaylistsScreenFragment();
        timeTableScreenFragment = new TimeTableScreenFragment();
        aboutScreenFragment = new AboutScreenFragment();
        settingsScreenFragment = new SettingsScreenFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .add(R.id.screen_fragment_container, mainScreenFragment, mainScreenFragment.getClass().getSimpleName())
                .hide(mainScreenFragment)

                .add(R.id.screen_fragment_container, playlistsScreenFragment, playlistsScreenFragment.getClass().getSimpleName())
                .hide(playlistsScreenFragment)

                .add(R.id.screen_fragment_container, timeTableScreenFragment, timeTableScreenFragment.getClass().getSimpleName())
                .hide(timeTableScreenFragment)

                .add(R.id.screen_fragment_container, aboutScreenFragment, aboutScreenFragment.getClass().getSimpleName())
                .hide(aboutScreenFragment)

                .add(R.id.screen_fragment_container, settingsScreenFragment, settingsScreenFragment.getClass().getSimpleName())
                .hide(settingsScreenFragment);


        transaction.commit();


        gotoFragment(mainScreenFragment);

        BubukaApplication.getInstance().setSyncListener(this);

        //startSync();

    }



    private void gotoFragment(Class<? extends Fragment> fragmentClazz) {
        gotoFragment(fragmentClazz.getSimpleName());
    }

    private void gotoFragment(String fragmentTag) {
        gotoFragment(getFragmentManager().findFragmentByTag(fragmentTag));
    }

    private void gotoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        for(int i = 0; i < backStackEntryCount; i++) {
            fragmentManager.popBackStack();
        }

       fragmentManager.beginTransaction()
                .show(fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }



    @Override
    protected void onDestroy() {
        BubukaApplication.getInstance().removeSyncListener();
        super.onDestroy();
    }

    @Override
    public void onMenuItemClick(NavigationAdapter.MenuItemId id) {
        Fragment targetFragment = null;
        switch (id) {
            case CURRENT_PLAY:
                targetFragment = mainScreenFragment;
                onActivatePlayer();
                break;
            case MY_FAST_TRACKS:
                targetFragment = mainScreenFragment;
                onActivatePlayer();
                break;
            case MUSIC:
                targetFragment = playlistsScreenFragment;
                onActivatePlaylists();
                break;
            case VIDEO:
                targetFragment = playlistsScreenFragment;
                onActivatePlaylists();
                break;
            case PHOTO:
                targetFragment = playlistsScreenFragment;
                onActivatePlaylists();
                break;
            case PLAYLISTS_BY_TIME:
                targetFragment = timeTableScreenFragment;
                onActivatePlaylists();
                break;
            case SETTINS:
                targetFragment = settingsScreenFragment;
                onActivatePlaylists();
                break;
            case ABOUT:
                targetFragment = aboutScreenFragment;
                onActivatePlaylists();
                break;
            case OBJECT_SELECTION:
                startActivity(new Intent(this, FullScreenActivity.class));
                break;
        }

        if(targetFragment != null) {
            gotoFragment(targetFragment);
        }
    }

    @Override
    public void onBackPressed() {
        gotoFragment(mainScreenFragment);
    }

    public void startSync() {
        BubukaApplication.getInstance().toggleSync();
    }

    @Override
    public void onProgress(SyncProgressReport progressReport) {
        if(mainScreenFragment != null) {
            MainFragment mainFragment = mainScreenFragment.getMainFragment();
            if(mainFragment != null) {
                mainFragment.receiveSyncProgress(progressReport);
            }
        }
    }

    @InjectView(R.id.bottom_player_layout) @Optional
    protected LinearLayout bottomPlayerLayout;

    @InjectView(R.id.bottom_player_text) @Optional
    protected TextView bottomPlayerText;

    @InjectView(R.id.bottom_playlists_text) @Optional
    protected TextView bottomPlaylistsText;

    @InjectView(R.id.bottom_player_current_track) @Optional
    protected TextView bottomPlayerCurrentTrack;

    @OnClick(R.id.botton_switcher_player) @Optional
    public void activatePlayer() {
        gotoFragment(mainScreenFragment);
        onActivatePlayer();
        updatePlayer();
    }

    @OnClick(R.id.botton_switcher_playlists) @Optional
    public void activatePlaylists() {
        gotoFragment(playlistsScreenFragment);
        onActivatePlaylists();
    }

    public void updatePlayer() {
        PlayerFragment playerFragment = mainScreenFragment.getPlayerFragment();
        if(playerFragment != null) {
            String musicTrackInfo = playerFragment.getCurrentMusicTrackInfo();
            if(musicTrackInfo != null) {
                bottomPlayerCurrentTrack.setText(musicTrackInfo);
                return;
            }

            String videoTrackInfo = playerFragment.getCurrentVideoTrackInfo();
            if(videoTrackInfo != null) {
                bottomPlayerCurrentTrack.setText(videoTrackInfo);
                return;
            }
        }
    }

    public void onActivatePlayer() {
        if(bottomPlayerLayout != null) {
            bottomPlayerLayout.setVisibility(View.GONE);
            bottomPlayerText.setTextColor(Color.parseColor("#e86f1c"));
            bottomPlaylistsText.setTextColor(Color.WHITE);

            bottomPlayerText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.player_hover, 0, 0, 0);
            bottomPlaylistsText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.playlist, 0, 0, 0);
        }
    }

    public void onActivatePlaylists() {
        if(bottomPlayerLayout != null) {
            bottomPlayerLayout.setVisibility(View.VISIBLE);
            bottomPlaylistsText.setTextColor(Color.parseColor("#e86f1c"));
            bottomPlayerText.setTextColor(Color.WHITE);

            bottomPlayerText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.player, 0, 0, 0);
            bottomPlaylistsText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.playlist_hover, 0, 0, 0);
        }
    }
}
