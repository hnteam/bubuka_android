package ru.espepe.bubuka.player.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.dao.query.WhereCondition;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.helper.ProgressBackgroundDrawable;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.service.SyncTask;

/**
 * Created by wolong on 11/08/14.
 */
public class MainFragment extends Fragment {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

    @InjectView(R.id.clips_counter_block) LinearLayout clipCounterBlock;
    @InjectView(R.id.photo_counter_block) LinearLayout photoCounterBlock;
    @InjectView(R.id.video_counter_block) LinearLayout videoCounterBlock;
    @InjectView(R.id.music_counter_block) LinearLayout musicCounterBlock;

    @InjectView(R.id.sync_status_line) TextView syncStatusLine;

    @OnClick(R.id.sync_status_line)
    public void syncClick() {
        ((MainActivity)getActivity()).startSync();
    }

    public void receiveSyncProgress(SyncTask.SyncProgressReport report) {
        if(report.type.equals("start")) {
            syncStatusLine.setText("Синхронизация...");
        } else if(report.type.equals("stop")) {
            updateSyncStatusView();
        } else if(report.type.equals("progress")) {
            for (SyncTask.SyncFileProgressReport fileReport : report.filesInProgress) {
                if (fileReport.fileType.equals("video")) {
                    setProgress(videoCounterBlock, fileReport);
                } else if (fileReport.fileType.equals("music")) {
                    setProgress(musicCounterBlock, fileReport);
                } else if (fileReport.fileType.equals("photo")) {
                    setProgress(photoCounterBlock, fileReport);
                } else if (fileReport.fileType.equals("clip")) {
                    setProgress(clipCounterBlock, fileReport);
                }
            }
        }

        updateView();
    }

    private void setProgress(View view, SyncTask.SyncFileProgressReport report) {
        if(report.type.equals("start")) {
            // reuse drawable
            //view.setBackgroundDrawable(new ProgressBackgroundDrawable((float) report.bytesDownloaded / (float) report.bytesTotal));
        } else if(report.type.equals("stop")) {
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_border));
        } else if(report.type.equals("progress")) {
            // TODO: reuse drawable
            view.setBackgroundDrawable(new ProgressBackgroundDrawable((float) report.bytesDownloaded / (float) report.bytesTotal));
        }
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
        updateSyncStatusView();
        return view;
    }

    private long countFilesByType(String type) {
        return BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder().where(StorageFileDao.Properties.Type.eq(type)).count();
    }

    public void updateView() {
        long clips = countFilesByType("clip");
        long photos = countFilesByType("photo");
        long video = countFilesByType("video");
        long music = countFilesByType("music");


        clipCounter.setText(Long.toString(clips));
        photoCounter.setText(Long.toString(photos));
        videoCounter.setText(Long.toString(video));
        musicCounter.setText(Long.toString(music));
    }


    public void updateSyncStatusView() {
        SyncStatus syncStatus = BubukaApplication.getInstance().getSyncStatus();
        switch (syncStatus.getType()) {
            case COMPLETED:
                syncStatusLine.setText(dateFormat.format(syncStatus.getDate().toString()));
                break;
            case INTERRUPTED:
                syncStatusLine.setText("Прервано: " + dateFormat.format(syncStatus.getDate()));
                break;
            case NOT_RUNNING:
                syncStatusLine.setText("Не синхронизировано");
                break;
        }
    }
}
