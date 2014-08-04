package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class FileObject {
    private String path;
    private String name;
    private int id;
    private int version;

    public FileObject() {
    }

    public FileObject(Element element) {
        this.id = Integer.parseInt(element.attr("id"));
        this.version = Integer.parseInt(element.attr("version"));
        this.name = element.attr("name");
        this.path = element.attr("path");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
