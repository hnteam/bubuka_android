package ru.espepe.bubuka.player.daogen;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.PropertyType;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class BubukaDaoGenerator {

    private static final int VERSION = 3;

    public static void main(String[] args) throws Exception {
        //System.out.println("hello world :D ");
        Schema schema = new Schema(VERSION, "ru.espepe.bubuka.player.dao");

        Entity storageFile = schema.addEntity("StorageFile");

        storageFile.addIdProperty().autoincrement();
        storageFile.addStringProperty("type"); // video | music | photo
        storageFile.addIntProperty("identity"); // file id in sync list
        storageFile.addStringProperty("name"); // file name in sync list
        storageFile.addStringProperty("path"); // file path in sync list
        storageFile.addIntProperty("version"); // file version in sync list
        storageFile.addStringProperty("status"); // file status: pending | active | cache

        Entity timelist = schema.addEntity("Timelist");
        timelist.addIdProperty().autoincrement();
        timelist.addIntProperty("priority");
        timelist.addStringProperty("name").unique();

        Entity play = schema.addEntity("Play");
        Property playId = play.addIdProperty().autoincrement().getProperty();
        play.addStringProperty("name");
        play.addIntProperty("volume");
        play.addIntProperty("time");
        play.addStringProperty("font");
        play.addStringProperty("fontnew");
        play.addBooleanProperty("anim");
        play.addIntProperty("period");



        Entity block = schema.addEntity("Block");
        block.addIdProperty().autoincrement();
        block.addStringProperty("name").getProperty();
        block.addStringProperty("mediadir");
        block.addIntProperty("fading");
        block.addBooleanProperty("loop");


        Entity track = schema.addEntity("Track");
        track.addIdProperty().autoincrement();
        track.addDateProperty("startDate");
        track.addDateProperty("endDate");
        track.addStringProperty("file");

        // 1 timelist <-> * timelist
        Property playTimelistId = play.addLongProperty("timelist_id").getProperty();
        play.addToOne(timelist, playTimelistId);
        timelist.addToMany(play, playTimelistId);

        // * play -> 1 block
        Property playBlockId = play.addLongProperty("block_id").getProperty();
        play.addToOne(block, playBlockId);
        block.addToMany(play, playBlockId);

        // * track -> 1 block
        Property trackBlockId = track.addLongProperty("block_id").getProperty();
        track.addToOne(block, trackBlockId);
        block.addToMany(track, trackBlockId);


        // track -> storage file
        Property trackFileId = track.addLongProperty("file_id").getProperty();
        track.addToOne(storageFile, trackFileId);

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
