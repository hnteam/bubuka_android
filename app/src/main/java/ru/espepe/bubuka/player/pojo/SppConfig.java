package ru.espepe.bubuka.player.pojo;

import android.util.ArrayMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wolong on 28/07/14.
 */
public class SppConfig extends PojoObject {
    private Map<String, TimeList> timeLists;
    private Map<String, Block> blocks;

    public SppConfig() {
    }

    public SppConfig(Document doc) {
        timeLists = new HashMap<String, TimeList>();
        for(Element timeListElement : doc.select("timelist")) {
            TimeList timeList = new TimeList(timeListElement);
            timeLists.put(timeList.getName(), timeList);
        }

        blocks = new HashMap<String, Block>();
        for(Element blockElement : doc.select("block")) {
            Block block = new Block(blockElement);
            blocks.put(block.getName(), block);
        }
    }

    public Map<String, TimeList> getTimeLists() {
        return timeLists;
    }

    public void setTimeLists(Map<String, TimeList> timeLists) {
        this.timeLists = timeLists;
    }

    public Map<String, Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<String, Block> blocks) {
        this.blocks = blocks;
    }
}
