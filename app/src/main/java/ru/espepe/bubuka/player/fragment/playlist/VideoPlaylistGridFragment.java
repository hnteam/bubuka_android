package ru.espepe.bubuka.player.fragment.playlist;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.adapter.FragmentStateArrayPagerAdapter;
import ru.espepe.bubuka.player.helper.NamedFragment;

/**
 * Created by wolong on 15/08/14.
 */
public class VideoPlaylistGridFragment extends NamedFragment {

    public VideoPlaylistGridFragment() {
        setName("Видео");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists_video, null);

        return view;
    }
}
