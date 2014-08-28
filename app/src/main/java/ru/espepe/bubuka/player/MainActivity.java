package ru.espepe.bubuka.player;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;


import ru.espepe.bubuka.player.adapter.NavigationAdapter;
import ru.espepe.bubuka.player.fragment.MainFragment;
import ru.espepe.bubuka.player.fragment.NavigationFragment;
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
                break;
            case MY_FAST_TRACKS:
                targetFragment = mainScreenFragment;
                break;
            case MUSIC:
                targetFragment = playlistsScreenFragment;
                break;
            case VIDEO:
                targetFragment = playlistsScreenFragment;
                break;
            case PHOTO:
                targetFragment = playlistsScreenFragment;
                break;
            case PLAYLISTS_BY_TIME:
                targetFragment = timeTableScreenFragment;
                break;
            case SETTINS:
                targetFragment = settingsScreenFragment;
                break;
            case ABOUT:
                targetFragment = aboutScreenFragment;
                break;
            case OBJECT_SELECTION:
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
}
