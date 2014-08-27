package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolong on 28/07/14.
 */
public class TimeListPojo extends PojoObject {
    private int priority;
    private String name;
    private List<PlayPojo> playList;

    public TimeListPojo() {
    }

    public TimeListPojo(Element element) {
        this.priority = Integer.parseInt(element.attr("priority"));
        this.name = element.attr("name");
        this.playList = new ArrayList<PlayPojo>();
        for(Element playElement : element.select("play")) {
            playList.add(new PlayPojo(playElement));
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayPojo> getPlayList() {
        return playList;
    }

    public void setPlayList(List<PlayPojo> playList) {
        this.playList = playList;
    }
}
