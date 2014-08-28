package ru.espepe.bubuka.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.Block;
import ru.espepe.bubuka.player.dao.BlockDao;
import ru.espepe.bubuka.player.dao.Play;
import ru.espepe.bubuka.player.dao.Timelist;
import ru.espepe.bubuka.player.dao.TimelistDao;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;

/**
 * Created by wolong on 14/08/14.
 */
public class TimeTableAdapter extends BaseExpandableListAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TimeTableAdapter.class);

    private final Context context;

    private TimeTableItem[] items;

    public TimeTableAdapter(Context context) {
        this.context = context;

        TimelistDao timelistDao = BubukaApplication.getInstance().getDaoSession().getTimelistDao();
        BlockDao blockDao = BubukaApplication.getInstance().getDaoSession().getBlockDao();





        List<Block> musicBlocks = blockDao.queryBuilder().where(BlockDao.Properties.Mediadir.eq("./music/")).list();
        List<Block> videoBlocks = blockDao.queryBuilder().where(BlockDao.Properties.Mediadir.eq("./video/")).list();

        TimeTableItem[] videoItems = new TimeTableItem[videoBlocks.size()];
        TimeTableItem[] musicItems = new TimeTableItem[musicBlocks.size()];

        for(int i = 0; i < videoBlocks.size(); i++) {
            videoItems[i] = new TimeTableChildItem(videoBlocks.get(i).getName());
        }

        for(int i = 0; i < musicBlocks.size(); i++) {
            musicItems[i] = new TimeTableChildItem(musicBlocks.get(i).getName());
        }

        String music6 = "Музыкальный плейлист не выбран";
        String video6 = "Видео плейлист не выбран";
        String music12 = "Музыкальный плейлист не выбран";
        String video12 = "Видео плейлист не выбран";
        String music18 = "Музыкальный плейлист не выбран";
        String video18 = "Видео плейлист не выбран";

        Timelist videoBackground = timelistDao.queryBuilder().where(TimelistDao.Properties.Name.eq("videobackground")).unique();
        if(videoBackground != null) {
            for(Play play : videoBackground.getPlayList()) {
                logger.info("video play time: {}, name: {}", play.getTime(), play.getBlock().getName());

                if(timeInInterval(play.getTime(), 6*60, 12*60)) {
                    video6 = play.getBlock().getName();
                } else if(timeInInterval(play.getTime(), 12*60, 18*60)) {
                    video12 = play.getBlock().getName();
                } else if(timeInInterval(play.getTime(), 18*60, 24*60)) {
                    video18 = play.getBlock().getName();
                }
            }
        }

        Timelist musicBackground = timelistDao.queryBuilder().where(TimelistDao.Properties.Name.eq("background")).unique();
        if(musicBackground != null) {
            for(Play play : musicBackground.getPlayList()) {
                logger.info("music play time: {}, name: {}", play.getTime(), play.getBlock().getName());

                if(timeInInterval(play.getTime(), 6*60, 12*60)) {
                    music6 = play.getBlock().getName();
                } else if(timeInInterval(play.getTime(), 12*60, 18*60)) {
                    music12 = play.getBlock().getName();
                } else if(timeInInterval(play.getTime(), 18*60, 24*60)) {
                    music18 = play.getBlock().getName();
                }
            }
        }

        items = new TimeTableItem[] {
                new TimeTableSectionItem("Утро (с 6 до 12)"),
                new TimeTableGroupItem(R.drawable.setting_play, music6, musicItems),
                new TimeTableGroupItem(R.drawable.setting_video, video6, videoItems),

                new TimeTableSectionItem("День (с 12 до 18)"),
                new TimeTableGroupItem(R.drawable.setting_play, music12, musicItems),
                new TimeTableGroupItem(R.drawable.setting_video, video12, videoItems),

                new TimeTableSectionItem("Вечер (с 18 до 24)"),
                new TimeTableGroupItem(R.drawable.setting_play, music18, musicItems),
                new TimeTableGroupItem(R.drawable.setting_video, video18, videoItems),
        };

        /*

        items = new TimeTableItem[] {

                new TimeTableSectionItem("Утро (с 6 до 12)"),
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
        */
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

    private static boolean timeInInterval(Integer time, int from, int to) {
        return time != null && time >= from && time < to;
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

            convertView.setEnabled(false);
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
