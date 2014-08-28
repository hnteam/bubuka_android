package ru.espepe.bubuka.player.fragment;

import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.adapter.PlaylistTrackAdapter;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.dao.TimelistDao;
import ru.espepe.bubuka.player.pojo.PlayList;

/**
 * Created by wolong on 27/08/14.
 */
public class CurrentPlaylistFragment extends Fragment {
    public static CurrentPlaylistFragment newInstance(String type) {
        CurrentPlaylistFragment fragment = new CurrentPlaylistFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_playlist, null);
        ButterKnife.inject(this, view);
        setupUi(inflater.getContext());
        return view;
    }

    @InjectView(R.id.current_playlist_track_list)
    protected ListView trackList;

    @InjectView(R.id.current_playlist_track_grid)
    protected GridView trackGrid;

    @InjectView(R.id.current_playlist_name)
    protected TextView currentPlaylistName;

    @InjectView(R.id.current_playlist_trackcount)
    protected TextView currentPlaylistCount;

    protected PlaylistTrackAdapter adapter;

    private void setupUi(Context context) {
        String type = getArguments().getString("type");

        adapter = new PlaylistTrackAdapter(context, type);

        if(type.equals("photo") || type.equals("video")) {
            trackList.setVisibility(View.INVISIBLE);
            trackGrid.setVisibility(View.VISIBLE);
            trackGrid.setAdapter(adapter);
        } else if(type.equals("music")) {
            trackGrid.setVisibility(View.INVISIBLE);
            trackList.setVisibility(View.VISIBLE);
            trackList.setAdapter(adapter);
        }

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                currentPlaylistCount.setText(String.format("%d треков", adapter.getCount()));
            }
        });

        PlayList currentPlaylist = BubukaApplication.getInstance().getCurrentPlayList(type);
        if(currentPlaylist != null) {
            currentPlaylistName.setText(currentPlaylist.getName());
        }

        adapter.updatePlaylists();
    }
}