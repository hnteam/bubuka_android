package ru.espepe.bubuka.player.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.dao.query.QueryBuilder;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.activity.CurrentPlaylistActivity;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.helper.ProgressBackgroundDrawable;
import ru.espepe.bubuka.player.pojo.SyncStatus;
import ru.espepe.bubuka.player.service.sync.SyncFileProgressReport;
import ru.espepe.bubuka.player.service.sync.SyncProgressReport;

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

    private void openCurrentPlaylist(String type) {
        CurrentPlaylistFragment fragment = (CurrentPlaylistFragment) getFragmentManager().findFragmentByTag(CurrentPlaylistFragment.class.getSimpleName());
        if(fragment != null) {
            fragment.changeType(type);
        } else {
            Intent intent = new Intent(getActivity(), CurrentPlaylistActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        }
    }

    @OnClick(R.id.clips_counter_block) void showClips() { openCurrentPlaylist("clip"); }
    @OnClick(R.id.photo_counter_block) void showPhoto() { openCurrentPlaylist("photo"); }
    @OnClick(R.id.video_counter_block) void showVideo() { openCurrentPlaylist("video"); }
    @OnClick(R.id.music_counter_block) void showMusic() { openCurrentPlaylist("music"); }

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

    public void receiveSyncProgress(SyncProgressReport report) {
        if(syncStatusLine == null) {
            return;
        }
        String type = report.getType();
        if(type.equals("start")) {
            syncStatusLine.setText("Синхронизация...");
        } else if(type.equals("stop")) {
            updateSyncStatusView();
            updateView();

        } else if(type.equals("progress")) {
            boolean needUpdateCounters = false;
            for (SyncFileProgressReport fileReport : report.getFilesInProgress()) {
                if(fileReport.type.equals("stop")) {
                    needUpdateCounters = true;
                }
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
            if(needUpdateCounters) {
                updateView();
            }
        }


    }

    private void setProgress(View view, SyncFileProgressReport report) {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.inject(this, view);
        updateView();
        updateSyncStatusView();
        return view;
    }

    private long countActiveFilesByType(String type) {
        return BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder()
                .where(
                        StorageFileDao.Properties.Type.eq(type),
                        StorageFileDao.Properties.Status.eq("active")
                ).count();
    }

    private long countCurrentFilesByType(String type) {
        QueryBuilder<StorageFile> queryBuilder = BubukaApplication.getInstance().getDaoSession().getStorageFileDao().queryBuilder();
        return queryBuilder.where(
                        StorageFileDao.Properties.Type.eq(type),
                        queryBuilder.or(
                                StorageFileDao.Properties.Status.eq("active"),
                                StorageFileDao.Properties.Status.eq("pending")
                        )
                ).count();
    }


    public void updateView() {
        long clipActive = countActiveFilesByType("clip");
        long photoActive = countActiveFilesByType("photo");
        long videoActive = countActiveFilesByType("video");
        long musicActive = countActiveFilesByType("music");

        long clipCurrent = countCurrentFilesByType("clip");
        long photoCurrent = countCurrentFilesByType("photo");
        long videoCurrent = countCurrentFilesByType("video");
        long musicCurrent = countCurrentFilesByType("music");


        clipCounter.setText("" + clipActive + "/" + clipCurrent);
        photoCounter.setText("" + photoActive + "/" + photoCurrent);
        videoCounter.setText("" + videoActive + "/" + videoCurrent);
        musicCounter.setText("" + musicActive + "/" + musicCurrent);
    }


    public void updateSyncStatusView() {
        SyncStatus syncStatus = BubukaApplication.getInstance().getSyncStatus();
        switch (syncStatus.getType()) {
            case COMPLETED:
                syncStatusLine.setText(dateFormat.format(syncStatus.getDate()));
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
