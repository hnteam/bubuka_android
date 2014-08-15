package ru.espepe.bubuka.player.parts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.espepe.bubuka.player.R;

/**
 * Created by wolong on 15/08/14.
 */
public class MusicPlaylistViewHolder {

    private int id;
    private String name;
    private boolean isCurrent;

    public MusicPlaylistViewHolder(int id, String name, boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.isCurrent = isCurrent;
    }

    public View getView(Context context, View convertView) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.playlist_item, null);
        }


        View currentFade = convertView.findViewById(R.id.playlist_item_current);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.playlist_item_image);
        TextView nameView = (TextView) convertView.findViewById(R.id.playlist_item_name);

        imageView.setImageResource(id);
        nameView.setText(name);


        if(isCurrent) {
            currentFade.setVisibility(View.VISIBLE);
        } else {
            currentFade.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}
