package ru.espepe.bubuka.player.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.adapter.PlaylistTrackAdapter;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.dao.TimelistDao;

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

    protected PlaylistTrackAdapter adapter;

    private void setupUi(Context context) {
        String type = getArguments().getString("type");
        //TimelistDao timelistDao = BubukaApplication.getInstance().getDaoSession().getTimelistDao();
        //timelistDao.queryBuilder().where(TimelistDao.Properties.Name.eq(typ))
        adapter = new PlaylistTrackAdapter(context, type);

        trackList.setAdapter(adapter);

        adapter.updatePlaylists();
    }
}
