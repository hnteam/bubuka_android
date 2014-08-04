package ru.espepe.bubuka.player.pojo;

import com.google.gson.Gson;

/**
 * Created by wolong on 28/07/14.
 */
public class PojoObject {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
