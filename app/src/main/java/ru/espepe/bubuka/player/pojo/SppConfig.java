package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wolong on 28/07/14.
 */
public class SppConfig extends PojoObject {
    private Map<String, TimeListPojo> timeLists;
    private Map<String, BlockPojo> blocks;

    public SppConfig() {
    }

    public SppConfig(Document doc) {
        timeLists = new HashMap<String, TimeListPojo>();
        for(Element timeListElement : doc.select("timelist")) {
            TimeListPojo timeList = new TimeListPojo(timeListElement);
            timeLists.put(timeList.getName(), timeList);
        }

        blocks = new HashMap<String, BlockPojo>();
        for(Element blockElement : doc.select("block")) {
            BlockPojo block = new BlockPojo(blockElement);
            blocks.put(block.getName(), block);
        }
    }

    public Map<String, TimeListPojo> getTimeLists() {
        return timeLists;
    }

    public void setTimeLists(Map<String, TimeListPojo> timeLists) {
        this.timeLists = timeLists;
    }

    public Map<String, BlockPojo> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<String, BlockPojo> blocks) {
        this.blocks = blocks;
    }
}
