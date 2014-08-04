package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class Track extends PojoObject {
    private String startDate;
    private String endDate;
    private String file;

    public Track(String startDate, String endDate, String file) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.file = file;
    }

    public Track() {
    }

    public Track(Element element) {
        this.startDate = element.attr("startdate");
        this.endDate = element.attr("enddate");
        this.file = element.attr("file");
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
