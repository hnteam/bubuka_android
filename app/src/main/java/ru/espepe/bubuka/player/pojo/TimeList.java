package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolong on 28/07/14.
 */
public class TimeList extends PojoObject {
    private int priority;
    private String name;
    private List<Play> playList;

    public TimeList() {
    }

    public TimeList(Element element) {
        this.priority = Integer.parseInt(element.attr("priority"));
        this.name = element.attr("name");
        this.playList = new ArrayList<Play>();
        for(Element playElement : element.select("play")) {
            playList.add(new Play(playElement));
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

    public List<Play> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Play> playList) {
        this.playList = playList;
    }
}
