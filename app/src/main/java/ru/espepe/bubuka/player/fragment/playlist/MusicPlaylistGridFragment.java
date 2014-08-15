package ru.espepe.bubuka.player.fragment.playlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.helper.MainHelper;
import ru.espepe.bubuka.player.helper.NamedFragment;
import ru.espepe.bubuka.player.parts.MusicPlaylistViewHolder;

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

    private void setupUI(Context context) {
        myPlaylistGrid.setAdapter(new ImageAdapter(context));
        preparedPlaylistGrid.setAdapter(new ImageAdapter(context));
    }

    public static class ImageAdapter extends BaseAdapter {
        private final Context context;

        public ImageAdapter(Context context) {
            this.context = context;
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

        private MusicPlaylistViewHolder[] images = new MusicPlaylistViewHolder[] {
                new MusicPlaylistViewHolder(R.drawable.logo, "Кофейня", false),
                new MusicPlaylistViewHolder(R.drawable.logo, "Супермаркет", false),
                new MusicPlaylistViewHolder(R.drawable.logo, "Бары и пабы", false),
                new MusicPlaylistViewHolder(R.drawable.logo, "Кофейня", false),
                new MusicPlaylistViewHolder(R.drawable.logo, "Супермаркет", true),
                new MusicPlaylistViewHolder(R.drawable.logo, "Бары и пабы", false),
        };


    }
}
