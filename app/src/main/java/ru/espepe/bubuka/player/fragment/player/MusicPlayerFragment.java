package ru.espepe.bubuka.player.fragment.player;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.fragment.PlayerFragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_music, null);

        return view;
    }
}
