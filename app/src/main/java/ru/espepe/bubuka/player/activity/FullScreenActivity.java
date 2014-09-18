package ru.espepe.bubuka.player.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.parts.PlayableTrack;
import ru.espepe.bubuka.player.service.PlayerService;

/**
 * Created by wolong on 29/08/14.
 */
public class FullScreenActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(FullScreenActivity.class);

    @InjectView(R.id.fullscreen_video_view)
    protected SurfaceView fullscreenVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ButterKnife.inject(this);
        setupUi();
    }

    private void setupUi() {


        fullscreenVideoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                PlayerService.getInstance().setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                PlayerService.getInstance().setDisplay(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        /*

        StorageFileDao storageFileDao = BubukaApplication.getInstance().getDaoSession().getStorageFileDao();
        List<StorageFile> files = storageFileDao.queryBuilder().where(StorageFileDao.Properties.Type.eq("video"), StorageFileDao.Properties.Status.eq("active")).list();

        if(files.isEmpty()) {
           return;
        }

        StorageFile storageFile = files.get(0);
        PlayableTrack track = PlayableTrack.from(storageFile);

        try {
            mediaPlayer.setDataSource(this, track.getUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            logger.error("failed to set datasource in fullscreen activity", e);
        }
        */
    }
}
