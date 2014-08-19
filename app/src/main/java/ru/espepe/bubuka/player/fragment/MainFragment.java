package ru.espepe.bubuka.player.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.dao.query.WhereCondition;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.pojo.SyncConfig;
import ru.espepe.bubuka.player.service.SyncTask;

/**
 * Created by wolong on 11/08/14.
 */
public class MainFragment extends Fragment {
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @InjectView(R.id.clips_counter) TextView clipCounter;
    @InjectView(R.id.photo_counter) TextView photoCounter;
    @InjectView(R.id.video_counter) TextView videoCounter;
    @InjectView(R.id.music_counter) TextView musicCounter;

    @OnClick(R.id.sync_status_line)
    public void syncClick() {
        ((MainActivity)getActivity()).startSync();
    }
/*
    @OnClick(R.id.button)
    protected void buttonOnClick() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Sync");
        progressDialog.setMessage("Media synchronization in progress...");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();


        SyncConfig config = new SyncConfig(getActivity().getExternalFilesDir(null), "http://bubuka.espepe.ru/users/", "testobject12345");
        new SyncTask() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();

                if(aBoolean) {
                    Toast.makeText(getActivity(), "Sync successfully completed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Sync failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onProgressUpdate(SyncProgressReport... values) {
                SyncProgressReport progress = values[0];

                progressDialog.setMax(progress.filesTotal);
                progressDialog.setProgress(progress.filesComplete);
                progressDialog.setMessage("Sync " + progress.currentFile);

            }
        }.execute(config);
    }
    */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.inject(this, view);
        updateView();
        return view;
    }

    private long countFilesByType(String type) {
        return BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder().where(StorageFileDao.Properties.Type.eq(type)).count();
    }

    private void updateView() {
        long clips = countFilesByType("clip");
        long photos = countFilesByType("photo");
        long video = countFilesByType("video");
        long music = countFilesByType("music");


        clipCounter.setText(Long.toString(clips));
        photoCounter.setText(Long.toString(photos));
        videoCounter.setText(Long.toString(video));
        musicCounter.setText(Long.toString(music));
    }
}
