package ru.espepe.bubuka.player.parts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import ru.espepe.bubuka.player.R;

/**
 * Created by wolong on 15/08/14.
 */
public class MusicPlaylistViewHolder {

    private Integer id;
    private String url;

    private String name;
    private boolean isCurrent;

    public MusicPlaylistViewHolder(int id, String name, boolean isCurrent) {
        this.id = id;
        this.url = null;
        this.name = name;
        this.isCurrent = isCurrent;
    }

    public MusicPlaylistViewHolder(String url, String name, boolean isCurrent) {
        this.id = null;
        this.url = url;
        this.name = name;
        this.isCurrent = isCurrent;
    }

    public View getView(Context context, View convertView) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.playlist_item, null);
        }


        View currentFade = convertView.findViewById(R.id.playlist_item_current);
        SmartImageView imageView = (SmartImageView) convertView.findViewById(R.id.playlist_item_image);
        TextView nameView = (TextView) convertView.findViewById(R.id.playlist_item_name);

        if(id != null) {
            imageView.setImageResource(id);
        } else if(url != null) {
            imageView.setImageUrl(url);
        }
        nameView.setText(name);


        if(isCurrent) {
            currentFade.setVisibility(View.VISIBLE);
        } else {
            currentFade.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}
