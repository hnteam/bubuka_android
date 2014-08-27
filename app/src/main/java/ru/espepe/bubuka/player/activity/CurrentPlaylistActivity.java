package ru.espepe.bubuka.player.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.fragment.CurrentPlaylistFragment;

/**
 * Created by wolong on 27/08/14.
 */
public class CurrentPlaylistActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_fragment);

        String type = getIntent().getStringExtra("type");
        Fragment fragment = CurrentPlaylistFragment.newInstance(type);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
