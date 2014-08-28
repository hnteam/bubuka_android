package ru.espepe.bubuka.player.fragment.playlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.helper.MainHelper;
import ru.espepe.bubuka.player.helper.NamedFragment;
import ru.espepe.bubuka.player.parts.MusicPlaylistViewHolder;
import ru.espepe.bubuka.player.pojo.PlayList;
import ru.espepe.bubuka.player.service.BubukaApi;

/**
 * Created by wolong on 15/08/14.
 */
public class MusicPlaylistGridFragment extends NamedFragment {
    public MusicPlaylistGridFragment() {
        setName("Музыка"); // TODO: localization
    }

    @InjectView(R.id.my_playlist_grid)
    protected GridView myPlaylistGrid;

    @InjectView(R.id.prepared_playlist_grid)
    protected GridView preparedPlaylistGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists_music, null);
        ButterKnife.inject(this, view);
        setupUI(inflater.getContext());
        return view;
    }

    private void setupUI(final Context context) {
        BubukaApi api = new BubukaApi();
        api.getPreparedPlaylists(context, new BubukaApi.RetrievePlaylistsListener() {
            @Override
            public void onPlaylistsSuccess(List<PlayList> playLists) {
                ArrayList<MusicPlaylistViewHolder> preparedPlaylists = new ArrayList<MusicPlaylistViewHolder>(playLists.size());
                for(PlayList playList : playLists) {
                    String imageUrl = "http://" + BubukaApplication.getInstance().getBubukaDomain() + playList.getImageUrl();
                    preparedPlaylists.add(new MusicPlaylistViewHolder(imageUrl, playList.getName(), playList.isActive()));
                }

                MusicPlaylistViewHolder[] list = preparedPlaylists.toArray(new MusicPlaylistViewHolder[preparedPlaylists.size()]);

                preparedPlaylistGrid.setAdapter(new ImageAdapter(context, list));
            }

            @Override
            public void onPlaylistsFailed() {

            }
        });

        api.getPlaylist(context, "music", new BubukaApi.RetrievePlaylistsListener() {
            @Override
            public void onPlaylistsSuccess(List<PlayList> playLists) {
                ArrayList<MusicPlaylistViewHolder> preparedPlaylists = new ArrayList<MusicPlaylistViewHolder>(playLists.size());
                for(PlayList playList : playLists) {
                    String imageUrl = "http://" + BubukaApplication.getInstance().getBubukaDomain() + playList.getImageUrl();
                    preparedPlaylists.add(new MusicPlaylistViewHolder(imageUrl, playList.getName(), playList.isActive()));
                }

                MusicPlaylistViewHolder[] list = preparedPlaylists.toArray(new MusicPlaylistViewHolder[preparedPlaylists.size()]);

                myPlaylistGrid.setAdapter(new ImageAdapter(context, list));
            }

            @Override
            public void onPlaylistsFailed() {

            }
        });
    }

    public static class ImageAdapter extends BaseAdapter {
        private final Context context;
        private MusicPlaylistViewHolder[] images;


        public ImageAdapter(Context context, MusicPlaylistViewHolder[] images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return images[position].getView(context, convertView);
        }




    }
}
