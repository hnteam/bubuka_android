package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolong on 28/07/14.
 */
public class BlockPojo extends PojoObject {
    private String name;
    private String mediaDir;
    private Integer fading;
    private boolean loop;
    private List<TrackPojo> tracks;

    public BlockPojo(Element element) {
        this.name = element.attr("name");
        this.mediaDir = element.attr("mediadir");
        this.fading = element.hasAttr("fading") ? Integer.parseInt(element.attr("fading")) : null;
        this.loop = element.hasAttr("loop") ? Boolean.parseBoolean(element.attr("loop")) : false;
        this.tracks = new ArrayList<TrackPojo>();
        for(Element trackElement : element.select("track")) {
            this.tracks.add(new TrackPojo(trackElement));
        }
    }

    public BlockPojo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaDir() {
        return mediaDir;
    }

    public void setMediaDir(String mediaDir) {
        this.mediaDir = mediaDir;
    }

    public Integer getFading() {
        return fading;
    }

    public void setFading(Integer fading) {
        this.fading = fading;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public List<TrackPojo> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackPojo> tracks) {
        this.tracks = tracks;
    }
}
