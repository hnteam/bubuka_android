package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;

/**
 * Created by wolong on 27/08/14.
 */
public class PlaylistTrackAdapter extends BaseAdapter {
    private Context context;
    private String type;
    private List<StorageFile> storageFiles;
    public PlaylistTrackAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public void updatePlaylists() {
        storageFiles = BubukaApplication.getInstance().getDaoSession().getStorageFileDao()
                .queryBuilder()
                    .where(StorageFileDao.Properties.Type.eq(type))
                .list();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(storageFiles == null) {
            return 0;
        }

        return storageFiles.size();
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
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.current_playlist_track_item, null);
        }

        StorageFile storageFile = storageFiles.get(position);
        TextView nameView = (TextView) convertView.findViewById(R.id.current_playlist_track_item_name);

        nameView.setText(storageFile.getName());

        return convertView;
    }
}
