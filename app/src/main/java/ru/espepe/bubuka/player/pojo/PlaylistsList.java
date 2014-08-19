package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolong on 15/08/14.
 */
public class PlaylistsList extends PojoObject {
    private List<PlayList> playLists;

    public PlaylistsList() {

    }

    public PlaylistsList(Document document) {
        playLists = new ArrayList<PlayList>();
        for(Element playlistElement : document.select("list")) {
            playLists.add(new PlayList(playlistElement));
        }
    }

    public List<PlayList> getPlayLists() {
        return playLists;
    }

    public void setPlayLists(List<PlayList> playLists) {
        this.playLists = playLists;
    }
}
