package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.parts.ImageGridItemViewHolder;

/**
 * Created by wolong on 27/08/14.
 */
public class PlaylistTrackAdapter extends BaseAdapter {
    private Context context;
    private String type;
    private List<StorageFile> storageFiles;
    private List<ImageGridItemViewHolder> imageGridItems;

    public PlaylistTrackAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public void updatePlaylists() {
        storageFiles = BubukaApplication.getInstance().getDaoSession().getStorageFileDao()
                .queryBuilder()
                    .where(StorageFileDao.Properties.Type.eq(type), StorageFileDao.Properties.Status.eq("active"))
                .list();

        if(type.equals("photo") || type.equals("video")) {
            imageGridItems = new ArrayList<ImageGridItemViewHolder>(storageFiles.size());
            for(StorageFile storageFile : storageFiles) {
                imageGridItems.add(new ImageGridItemViewHolder(storageFile));
            }
        }
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
        if (imageGridItems != null) {
            return imageGridItems.get(position).getView(context, convertView);
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.current_playlist_track_item, null);
            }


            StorageFile storageFile = storageFiles.get(position);

            TextView nameView = (TextView) convertView.findViewById(R.id.current_playlist_track_item_name);


            nameView.setText(storageFile.getName());
            return convertView;
        }
    }
}
