package ru.espepe.bubuka.player.service;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;
import ru.espepe.bubuka.player.pojo.Domain;
import ru.espepe.bubuka.player.pojo.PlayList;
import ru.espepe.bubuka.player.pojo.PlaylistsList;
import ru.espepe.bubuka.player.pojo.SppConfig;
import ru.espepe.bubuka.player.pojo.SyncList;
import ru.espepe.bubuka.player.pojo.SyncListFiles;

/**
 * Created by wolong on 28/07/14.
 */
public class BubukaApi {
    private static final Logger logger = LoggerFactory.getLogger(BubukaApi.class);

    private final String domain;
    private final String objectCode;

    public BubukaApi(String domain, String objectCode) {
        this.domain = domain;
        this.objectCode = objectCode;
    }

    public BubukaApi() {
        this.domain = BubukaApplication.getInstance().getBubukaDomain();
        this.objectCode = BubukaApplication.getInstance().getObjectCode();
    }

    public SppConfig syncConfig(List<Domain> domains) throws IOException {
        SppConfig config = null;
        for(Domain currentDomain : domains) {
            try {
                config = findConfig(currentDomain.getUrl());
                break;
            } catch (IOException e) {
                logger.info("failed to retrieve config from mirror: {}", currentDomain);
            }
        }

        if(config == null) {
            logger.warn("failed to retrieve config");
            throw new IOException("failed to get config");
        }

        logger.info("config successfully retrieved: {}", config);

        return config;
    }

    public SyncListFiles syncFiles(List<Domain> domains, String type) throws IOException {
        for(Domain domain : domains) {
            try {
                InputStream inputStream = new URL(domain.getUrl() + objectCode + "/" + type + "/.sync.xml").openStream();
                return new SyncListFiles(Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser()));
            } catch (Exception e) {
                logger.warn("failed to sync {} with mirror {}", type, domain.getUrl());
            }
        }

        throw new IOException("failed to sync "+type);
    }

    public SyncListFiles syncFiles(String currentDomain, String type) throws IOException {
        InputStream inputStream = new URL(currentDomain + objectCode + "/"+type+"/.sync.xml").openStream();
        return new SyncListFiles(Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser()));
    }


    public SyncList findSyncList() throws IOException {
        InputStream inputStream = new URL("http://" + domain + "/users/" + objectCode + "/.sync.xml").openStream();
        return new SyncList(Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser()));
    }

    private SppConfig findConfig(String currentDomain) throws IOException {
        InputStream inputStream = new URL(currentDomain + objectCode + "/timelist.xml").openStream();
        return new SppConfig(Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser()));
    }

    AsyncHttpClient http = new AsyncHttpClient();

    public void getPreparedPlaylists(Context context, final RetrievePlaylistsListener listener) {
        http.get(context, "http://"+domain+"/users/"+objectCode+"/player/?subaction=readylist", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    PlaylistsList playlistsList = new PlaylistsList(Jsoup.parse(new ByteArrayInputStream(responseBody), "UTF-8", "", Parser.xmlParser()));
                    listener.onPlaylistsSuccess(playlistsList.getPlayLists());
                } catch (IOException e) {
                    logger.error("failed to retrieve prepared playlists", e);
                    listener.onPlaylistsFailed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.onPlaylistsFailed();
            }
        });
    }

    public void getPlaylist(Context context, String type, final RetrievePlaylistsListener listener) {
        http.get(context, "http://"+domain+"/users/"+objectCode+"/player/?subaction=own" + type, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    PlaylistsList playlistsList = new PlaylistsList(Jsoup.parse(new ByteArrayInputStream(responseBody), "UTF-8", "", Parser.xmlParser()));
                    listener.onPlaylistsSuccess(playlistsList.getPlayLists());
                } catch (IOException e) {
                    logger.error("failed to retrieve playlists", e);
                    listener.onPlaylistsFailed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                logger.warn("failed to retrieve playlists", error);
                listener.onPlaylistsFailed();
            }
        });
    }

    public static interface RetrievePlaylistsListener {
        void onPlaylistsSuccess(List<PlayList> playLists);
        void onPlaylistsFailed();
    }
}
