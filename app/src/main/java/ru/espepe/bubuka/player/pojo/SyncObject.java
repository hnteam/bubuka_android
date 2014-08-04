package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class SyncObject extends PojoObject {
    private String code;
    private String name;
    private String path;
    private int version;
    private String disable;

    public SyncObject(Element object) {
        name = object.attr("name");
        code = object.attr("code");
        path = object.attr("path");
        version = Integer.parseInt(object.attr("version"));
        disable = object.attr("disable");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
    }


}
