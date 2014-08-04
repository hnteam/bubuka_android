package ru.espepe.bubuka.player.daogen;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class BubukaDaoGenerator {

    private static final int VERSION = 2;

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

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
