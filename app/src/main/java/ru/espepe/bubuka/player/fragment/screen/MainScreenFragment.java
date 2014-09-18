package ru.espepe.bubuka.player.fragment.screen;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.fragment.CurrentPlaylistFragment;
import ru.espepe.bubuka.player.fragment.MainFragment;
import ru.espepe.bubuka.player.fragment.PlayerFragment;

/**
 * Created by wolong on 13/08/14.
 */
public class MainScreenFragment extends Fragment {

    CurrentPlaylistFragment currentPlaylistFragment;
    PlayerFragment playerFragment = new PlayerFragment();
    MainFragment mainFragment = new MainFragment();

    private CurrentPlaylistFragment getCurrentPlaylistFragment() {
        if(currentPlaylistFragment == null) {
            currentPlaylistFragment = CurrentPlaylistFragment.newInstance("photo");
        }

        return currentPlaylistFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_main, null);

        FrameLayout playlistLayout = (FrameLayout) view.findViewById(R.id.main_playlist_layout);
        if(playlistLayout != null) {
            getFragmentManager().beginTransaction().replace(R.id.main_playlist_layout, getCurrentPlaylistFragment(), CurrentPlaylistFragment.class.getSimpleName()).commit();
        }



        View playerSwipeFragmentContainer = view.findViewById(R.id.player_swipe_fragment_container);
        if(playerSwipeFragmentContainer != null) {
            getFragmentManager().beginTransaction().replace(R.id.player_swipe_fragment_container, playerFragment).commit();
        }

        View mainContainer = view.findViewById(R.id.fragment_main_container);
        if(mainContainer != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_main_container, mainFragment).commit();
        }



        return view;
    }

    public MainFragment getMainFragment() {
        //return mainFragment;
        return (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
    }

    public PlayerFragment getPlayerFragment() {
        //return playerFragment;
        return (PlayerFragment) getFragmentManager().findFragmentByTag("player_swipe_fragment");
        //return (PlayerFragment) getFragmentManager().findFragmentById(R.id.player_swipe_fragment);
    }
}
