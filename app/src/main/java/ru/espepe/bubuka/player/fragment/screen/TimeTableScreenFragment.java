package ru.espepe.bubuka.player.fragment.screen;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.adapter.TimeTableAdapter;

/**
 * Created by wolong on 14/08/14.
 */
public class TimeTableScreenFragment extends Fragment {
    @InjectView(R.id.timetable_list)
    protected ExpandableListView timetableList;

    private ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_timetable, null);
        ButterKnife.inject(this, view);
        setupUi(inflater.getContext());
        return view;
    }

    private void setupUi(Context context) {
        adapter = new TimeTableAdapter(context);
        timetableList.setAdapter(adapter);

        Drawable indicator = context.getResources().getDrawable(R.drawable.timetable_group_indicator);
        timetableList.setGroupIndicator(indicator);


        int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        int rightBound = displayWidth - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        int leftBound = rightBound - indicator.getIntrinsicWidth();


        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            timetableList.setIndicatorBounds(leftBound, rightBound);
        } else {
            timetableList.setIndicatorBoundsRelative(leftBound, rightBound);
        }
    }
}
