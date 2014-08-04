package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Element;

/**
 * Created by wolong on 28/07/14.
 */
public class Play extends PojoObject {
    private int volume;
    private String time;
    private String block;
    private String name;

    private String font;
    private String fontnew;
    private String anim;
    private String period;

    public Play() {

    }

    public Play(Element element) {
        this.volume = element.hasAttr("volume") ? Integer.parseInt(element.attr("volume")) : -1;

        // font, fontnew, anim, period
        this.font = element.hasAttr("font") ? element.attr("font") : null;
        this.fontnew = element.hasAttr("fontnew") ? element.attr("fontnew") : null;
        this.anim = element.hasAttr("anim") ? element.attr("anim") : null;
        this.period = element.hasAttr("period") ? element.attr("period") : null;

        this.time = element.attr("time");
        this.block = element.attr("block");
        this.name = element.attr("name");
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getTime() {
        return time;
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

    public String getAnim() {
        return anim;
    }

    public void setAnim(String anim) {
        this.anim = anim;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
