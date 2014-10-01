package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.helper.MenuItemId;
import ru.espepe.bubuka.player.helper.OnMenuItemListener;

/**
 * Created by wolong on 11/08/14.
 */
public class NavigationAdapter extends BaseAdapter {
    private final Context context;
    private final OnMenuItemListener listener;

    public NavigationAdapter(Context context, OnMenuItemListener listener) {
        this.context = context;
        this.listener = listener;
        items = new NavigationItem[] {
                new NavigationItemEntity(context.getString(R.string.menu_current_play), MenuItemId.CURRENT_PLAY),
                new NavigationItemEntity(context.getString(R.string.menu_my_fast_tracks), MenuItemId.MY_FAST_TRACKS),

                new NavigationItemSection(context.getString(R.string.menu_playlists_section_title)),
                new NavigationItemEntity(context.getString(R.string.menu_music), MenuItemId.MUSIC),
                new NavigationItemEntity(context.getString(R.string.menu_photo), MenuItemId.PHOTO),
                new NavigationItemEntity(context.getString(R.string.menu_video), MenuItemId.VIDEO),
                new NavigationItemEntity(context.getString(R.string.menu_playlists_by_time), MenuItemId.PLAYLISTS_BY_TIME),

                new NavigationItemSection(context.getString(R.string.menu_about_section)),
                new NavigationItemEntity(context.getString(R.string.menu_settings), MenuItemId.SETTINS),
                new NavigationItemEntity(context.getString(R.string.menu_about_app), MenuItemId.ABOUT),

                new NavigationItemSection("example@postmail.ru"),
                new NavigationItemEntity(context.getString(R.string.menu_object_selection), MenuItemId.OBJECT_SELECTION),
        };
    }

    private void handleMenuClick(MenuItemId id) {
        final OnMenuItemListener listener = this.listener;
        if(listener != null) {
            listener.onMenuItemClick(id);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
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

    private NavigationItem[] items;

    private abstract class NavigationItem {
        abstract int getType();
        abstract View getView(Context context, View convertView);
    }

    private class NavigationItemSection extends NavigationItem {
        private final String text;

        private NavigationItemSection(String text) {
            this.text = text;
        }

        @Override
        int getType() {
            return 0;
        }

        @Override
        View getView(Context context, View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.navigation_item_section, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.section_name);

            textView.setText(text);

            return convertView;
        }
    }

    private class NavigationItemEntity extends NavigationItem implements View.OnClickListener {
        private final String text;
        private final MenuItemId id;

        private NavigationItemEntity(String text, MenuItemId id) {
            this.text = text;
            this.id = id;
        }

        @Override
        int getType() {
            return 1;
        }

        @Override
        View getView(Context context, View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.navigation_item_entity, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.entity_text);

            textView.setText(text);

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
}
