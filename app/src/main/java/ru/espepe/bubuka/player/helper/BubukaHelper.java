package ru.espepe.bubuka.player.helper;

import java.util.Date;

import ru.espepe.bubuka.player.dao.Track;

/**
 * Created by wolong on 01/10/14.
 */
public class BubukaHelper {
    public static boolean isValidDateInterval(Track track) {
        final Date currentDate = new Date();
        return currentDate.after(track.getStartDate()) && currentDate.before(track.getEndDate());
    }
}
