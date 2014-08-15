package ru.espepe.bubuka.player.fragment.screen;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.adapter.FragmentStateArrayPagerAdapter;
import ru.espepe.bubuka.player.fragment.playlist.MusicPlaylistGridFragment;
import ru.espepe.bubuka.player.fragment.playlist.PhotoPlaylistGridFragment;
import ru.espepe.bubuka.player.fragment.playlist.VideoPlaylistGridFragment;

/**
 * Created by wolong on 13/08/14.
 */
public class PlaylistsScreenFragment extends Fragment {

    @InjectView(R.id.playlist_view_pager)
    protected ViewPager playlistViewPager;

    @InjectView(R.id.playlist_tabs)
    protected PagerSlidingTabStrip playlistTabs;

    private FragmentStateArrayPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, null);
        ButterKnife.inject(this, view);
        setupUI();
        return view;
    }

    private void setupUI() {
        playlistTabs.setShouldExpand(true);
        adapter = new FragmentStateArrayPagerAdapter(getFragmentManager(), new MusicPlaylistGridFragment(), new PhotoPlaylistGridFragment(), new VideoPlaylistGridFragment());
        playlistViewPager.setAdapter(adapter);
        playlistTabs.setViewPager(playlistViewPager);
        playlistTabs.setIndicatorColor(Color.parseColor("#e86f1c"));

    }
}
