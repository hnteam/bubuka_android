package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 15/08/14.
 */
public class PlayList extends PojoObject {
    private int id;
    private String name;
    private String imageUrl;
    private boolean active;

    public PlayList() {
    }

    public PlayList(Element playlistElement) {
        this.id = Integer.parseInt(playlistElement.attr("id"));
        this.name = playlistElement.attr("name");
        this.imageUrl = playlistElement.attr("img");
        this.active = playlistElement.hasAttr("active");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
