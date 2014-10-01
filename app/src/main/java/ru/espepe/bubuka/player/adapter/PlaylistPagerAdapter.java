package ru.espepe.bubuka.player.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by wolong on 14/08/14.
 */
public class PlaylistPagerAdapter extends FragmentStatePagerAdapter {
    public PlaylistPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override @SuppressWarnings("NewApi")
    public void destroyItem(ViewGroup container, int position, Object object) {
        android.app.Fragment fragment = (android.app.Fragment) object;
        try {
            fragment.setUserVisibleHint(true);
        } catch (Throwable e) {}
        super.destroyItem(container, position, object);
    }
}
