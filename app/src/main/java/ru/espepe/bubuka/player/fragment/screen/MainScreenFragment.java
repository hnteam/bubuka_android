package ru.espepe.bubuka.player.fragment.screen;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.fragment.MainFragment;
import ru.espepe.bubuka.player.fragment.PlayerFragment;

/**
 * Created by wolong on 13/08/14.
 */
public class MainScreenFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_main, null);

        return view;
    }

    public MainFragment getMainFragment() {
        return (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
    }

    public PlayerFragment getPlayerFragment() {
        return (PlayerFragment) getFragmentManager().findFragmentByTag("player_swipe_fragment");
        //return (PlayerFragment) getFragmentManager().findFragmentById(R.id.player_swipe_fragment);
    }
}
