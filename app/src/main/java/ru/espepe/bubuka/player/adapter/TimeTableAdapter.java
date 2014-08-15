package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import ru.espepe.bubuka.player.R;

/**
 * Created by wolong on 14/08/14.
 */
public class TimeTableAdapter extends BaseExpandableListAdapter {
    private final Context context;

    private TimeTableItem[] items;

    public TimeTableAdapter(Context context) {
        this.context = context;

        items = new TimeTableItem[] {

                new TimeTableSectionItem("Утро (с 8 до 12)"),
                new TimeTableGroupItem(R.drawable.setting_play, "Музыкальный плейлист не выбран", new TimeTableItem[]{
                        new TimeTableChildItem("Music item 1"),
                        new TimeTableChildItem("Music item 2"),
                        new TimeTableChildItem("Music item 3"),
                }),
                new TimeTableGroupItem(R.drawable.setting_video, "Видео плейлист не выбран", new TimeTableItem[] {
                        new TimeTableChildItem("Video item 1"),
                        new TimeTableChildItem("Video item 2"),
                        new TimeTableChildItem("Video item 3")
                }),


                new TimeTableSectionItem("День (с 12 до 18)"),
                new TimeTableGroupItem(R.drawable.setting_play, "Музыкальный плейлист не выбран", new TimeTableItem[]{
                        new TimeTableChildItem("Music item 1"),
                        new TimeTableChildItem("Music item 2"),
                        new TimeTableChildItem("Music item 3"),
                }),
                new TimeTableGroupItem(R.drawable.setting_video, "Видео плейлист не выбран", new TimeTableItem[] {
                        new TimeTableChildItem("Video item 1"),
                        new TimeTableChildItem("Video item 2"),
                        new TimeTableChildItem("Video item 3")
                }),


                new TimeTableSectionItem("Вечер (с 18 до 24)"),
                new TimeTableGroupItem(R.drawable.setting_play, "Музыкальный плейлист не выбран", new TimeTableItem[]{
                        new TimeTableChildItem("Music item 1"),
                        new TimeTableChildItem("Music item 2"),
                        new TimeTableChildItem("Music item 3"),
                }),
                new TimeTableGroupItem(R.drawable.setting_video, "Видео плейлист не выбран", new TimeTableItem[] {
                        new TimeTableChildItem("Video item 1"),
                        new TimeTableChildItem("Video item 2"),
                        new TimeTableChildItem("Video item 3")
                }),
        };
    }

    @Override
    public int getGroupCount() {
        return items.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items[groupPosition].getChildCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items[groupPosition].getChild(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return items[groupPosition].getView(convertView);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return items[groupPosition].getChild(childPosition).getView(convertView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupType(int groupPosition) {
        return items[groupPosition].getType();
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return items[groupPosition].getChild(childPosition).getType();
    }

    @Override
    public int getChildTypeCount() {
        return 1;
    }



    private interface TimeTableItem {
        View getView(View convertView);
        int getChildCount();
        TimeTableItem getChild(int position);
        int getType();
    }

    private class TimeTableGroupItem implements TimeTableItem {
        private final int icon;
        private final String text;
        private final TimeTableItem[] childs;

        private TimeTableGroupItem(int icon, String text, TimeTableItem[] childs) {
            this.icon = icon;
            this.text = text;
            this.childs = childs;
        }


        @Override
        public View getView(View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.time_table_group_item, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.timetable_group_name);
            textView.setText(text);
            textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(icon), null, null, null);

            return convertView;
        }

        @Override
        public int getChildCount() {
            return childs.length;
        }

        @Override
        public TimeTableItem getChild(int position) {
            return childs[position];
        }

        @Override
        public int getType() {
            return 0;
        }
    }

    private class TimeTableSectionItem implements TimeTableItem {
        private final String text;

        private TimeTableSectionItem(String text) {
            this.text = text;
        }


        @Override
        public View getView(View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.time_table_section_item, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.timetable_section_name);
            textView.setText(text);

            return convertView;
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public TimeTableItem getChild(int position) {
            return null;
        }

        @Override
        public int getType() {
            return 1;
        }
    }

    private class TimeTableChildItem implements TimeTableItem {
        private final String text;

        private TimeTableChildItem(String text) {
            this.text = text;
        }

        @Override
        public View getView(View convertView) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.time_table_child_item, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.timetable_child_name);
            textView.setText(text);

            return convertView;
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public TimeTableItem getChild(int position) {
            return null;
        }

        @Override
        public int getType() {
            return 0;
        }
    }
}
