package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.helper.MenuItemId;
import ru.espepe.bubuka.player.helper.OnMenuItemListener;

/**
 * Created by wolong on 29/09/14.
 */
public class NavigationTableAdapter extends BaseAdapter {
    private final Context context;
    private final OnMenuItemListener listener;

    private NavigationItem[] items;

    public NavigationTableAdapter(Context context, OnMenuItemListener listener) {
        this.context = context;
        this.listener = listener;

        items = new NavigationItem[] {
                new NavigationItem(R.drawable.nav_plaing, R.drawable.nav_plaing_hover, "Сейчас играет", MenuItemId.CURRENT_PLAY),
                new NavigationItem(R.drawable.nav_music, R.drawable.nav_music_hover, "Музыка", MenuItemId.MUSIC),
                new NavigationItem(R.drawable.nav_photo, R.drawable.nav_photo_hover, "Фото", MenuItemId.PHOTO),
                new NavigationItem(R.drawable.nav_video, R.drawable.nav_video_hover, "Видео", MenuItemId.VIDEO),
                new NavigationItem(R.drawable.nav_pin, R.drawable.nav_pin_hover, "Выбор объекта", MenuItemId.OBJECT_SELECTION),
                new NavigationItem(R.drawable.nav_soundtime, R.drawable.nav_soundtime_hover, "Прейлисты", MenuItemId.PLAYLISTS_BY_TIME),
                new NavigationItem(R.drawable.nav_setting, R.drawable.nav_setting_hover, "Настройки", MenuItemId.SETTINS)
        };
    }


    private class NavigationItem implements View.OnClickListener {
        private final String text;
        private final MenuItemId id;
        private final int iconId;
        private final int iconHoverId;

        private NavigationItem(int icon, int iconHoverId, String text, MenuItemId id) {
            this.iconId = icon;
            this.iconHoverId = iconHoverId;
            this.text = text;
            this.id = id;
        }

        View getView(Context context, View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.navigation_item_tablet_entity, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.entity_text);
            textView.setText(text);
            textView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
            convertView.setOnClickListener(this);

            return convertView;
        }

        @Override
        public void onClick(View v) {
            if(id != null) {
                handleMenuClick(id);
            }
        }
    }

    protected void handleMenuClick(MenuItemId id) {
        final OnMenuItemListener listener = this.listener;
        if(listener != null) {
            listener.onMenuItemClick(id);
        }
    }


    @Override
    public int getCount() {
        return items.length;
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
        return items[position].getView(context, convertView);
    }
}
