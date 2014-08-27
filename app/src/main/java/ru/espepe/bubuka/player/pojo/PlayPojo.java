package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class PlayPojo extends PojoObject {
    private Integer volume;
    private String time;
    private String block;
    private String name;

    private String font;
    private String fontnew;
    private String anim;
    private Integer period;

    public PlayPojo() {

    }

    public PlayPojo(Element element) {
        this.volume = element.hasAttr("volume") ? Integer.parseInt(element.attr("volume")) : null;

        // font, fontnew, anim, period
        this.font = element.hasAttr("font") ? element.attr("font") : null;
        this.fontnew = element.hasAttr("fontnew") ? element.attr("fontnew") : null;
        this.anim = element.hasAttr("anim") ? element.attr("anim") : null;
        this.period = element.hasAttr("period") ? Integer.parseInt(element.attr("period")) : null;

        this.time = element.attr("time");
        this.block = element.attr("block");
        this.name = element.attr("name");
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getTime() {
        return time;
    }

    public Integer getTimeMinuts() {
        if(time == null) {
            return null;
        }

        String[] parts = time.split(":");
        if(parts.length != 2) {
            return null;
        }

        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFontnew() {
        return fontnew;
    }

    public void setFontnew(String fontnew) {
        this.fontnew = fontnew;
    }

    public Boolean getAnim() {
        return anim != null && "true".equals(anim);
    }

    public void setAnim(String anim) {
        this.anim = anim;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}
