package ru.espepe.bubuka.player.pojo;


import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class Domain extends PojoObject {
    private String url;
    private int priority;

    public Domain(String url, int priority) {
        this.url = url;
        this.priority = priority;
    }

    public Domain() {
    }

    public Domain(Element domainElement) {
        url = domainElement.attr("url");
        priority = Integer.parseInt(domainElement.attr("priority"));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


}
