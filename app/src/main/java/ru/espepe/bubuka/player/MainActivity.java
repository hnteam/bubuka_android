package ru.espepe.bubuka.player;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.crypto.Crypto;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import ru.espepe.bubuka.player.adapter.NavigationAdapter;
import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.fragment.NavigationFragment;
import ru.espepe.bubuka.player.fragment.screen.MainScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.PlaylistsScreenFragment;
import ru.espepe.bubuka.player.fragment.screen.TimeTableScreenFragment;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.SyncConfig;
import ru.espepe.bubuka.player.service.BubukaApi;
import ru.espepe.bubuka.player.service.BubukaServiceConnector;
import ru.espepe.bubuka.player.service.SyncTask;


public class MainActivity extends Activity implements NavigationAdapter.OnMenuItemListener {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private NavigationFragment navigationFragment;

    // screen fragments
    private MainScreenFragment mainScreenFragment;
    private PlaylistsScreenFragment playlistsScreenFragment;
    private TimeTableScreenFragment timeTableScreenFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e86f1c")));


        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.main_navigation_fragment);
        navigationFragment.setUp(R.id.main_navigation_fragment, (DrawerLayout) findViewById(R.id.main_drawer_layout));

        mainScreenFragment = new MainScreenFragment();
        playlistsScreenFragment = new PlaylistsScreenFragment();
        timeTableScreenFragment = new TimeTableScreenFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .add(R.id.screen_fragment_container, mainScreenFragment, mainScreenFragment.getClass().getSimpleName())
                .hide(mainScreenFragment)

                .add(R.id.screen_fragment_container, playlistsScreenFragment, playlistsScreenFragment.getClass().getSimpleName())
                .hide(playlistsScreenFragment)

                .add(R.id.screen_fragment_container, timeTableScreenFragment, timeTableScreenFragment.getClass().getSimpleName())
                .hide(timeTableScreenFragment);


        transaction.commit();


        gotoFragment(mainScreenFragment);
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
        //bubukaServiceConnector.onDestroy();
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
                break;
            case ABOUT:
                break;
            case OBJECT_SELECTION:
                break;
        }

        if(targetFragment != null) {
            gotoFragment(targetFragment);
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoMainScreen();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onBackPressed() {
        gotoFragment(mainScreenFragment);
    }
}
