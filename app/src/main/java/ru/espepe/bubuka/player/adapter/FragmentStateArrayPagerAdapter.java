package ru.espepe.bubuka.player.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import ru.espepe.bubuka.player.helper.NamedFragment;

/**
 * Created by wolong on 15/08/14.
 */
public class FragmentStateArrayPagerAdapter extends FragmentStatePagerAdapter {
    private final NamedFragment[] fragments;
    private final FragmentManager fm;

    public FragmentStateArrayPagerAdapter(FragmentManager fm, NamedFragment... fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final String name = fragments[position].getName();
        return name == null ? "NONAME" : name;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        fm.saveFragmentInstanceState(fragment);

        super.destroyItem(container, position, object);
    }
}
