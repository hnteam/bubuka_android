package ru.espepe.bubuka.player;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.crypto.Crypto;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import ru.espepe.bubuka.player.dao.DaoSession;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.dao.StorageFileDao;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.SyncConfig;
import ru.espepe.bubuka.player.service.BubukaApi;
import ru.espepe.bubuka.player.service.BubukaServiceConnector;
import ru.espepe.bubuka.player.service.SyncTask;


public class MainActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private BubukaServiceConnector bubukaServiceConnector = new BubukaServiceConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                progressDialog.setIndeterminate(false);
                progressDialog.setTitle("Sync");
                progressDialog.setMessage("Media synchronization in progress...");
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();


                SyncConfig config = new SyncConfig(getExternalFilesDir(null), "http://bubuka.espepe.ru/users/", "testobject12345");
                new SyncTask() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        progressDialog.dismiss();

                        if(aBoolean) {
                            Toast.makeText(MainActivity.this, "Sync successfully completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
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
        });


        bubukaServiceConnector.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        bubukaServiceConnector.onDestroy();
        super.onDestroy();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
