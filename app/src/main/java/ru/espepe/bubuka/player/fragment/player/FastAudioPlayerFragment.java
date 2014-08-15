package ru.espepe.bubuka.player.fragment.player;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.espepe.bubuka.player.R;

/**
 * Created by wolong on 12/08/14.
 */
public class FastAudioPlayerFragment extends Fragment {
    public static FastAudioPlayerFragment newInstance() {
        FastAudioPlayerFragment fragment = new FastAudioPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView view = (ListView) inflater.inflate(R.layout.fragment_player_fast_audio, null);

        final Item[] items = new Item[100];
        for(int i = 0; i < 100; i++) {
            items[i] = new Item("Трек " + i);
        }

        view.setAdapter(new BaseAdapter() {
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
                return items[position].getView(inflater.getContext(), convertView);
            }
        });

        return view;
    }

    private static class Item {
        private final String title;

        public Item(String title) {
            this.title = title;
        }

        public View getView(Context context, View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fast_audio_list_item, null);
            }

            TextView itemText = (TextView) convertView.findViewById(R.id.fast_audio_item_text);
            ImageView itemIcon = (ImageView) convertView.findViewById(R.id.fast_audio_item_icon);

            itemText.setText(title);

            return convertView;
        }
    }
}
