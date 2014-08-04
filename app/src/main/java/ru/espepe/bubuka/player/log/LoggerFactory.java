package ru.espepe.bubuka.player.log;

import java.util.HashMap;

/**
 * Created by wolong on 23/06/14.
 */
public final class LoggerFactory {
    private static final HashMap<Class<?>, Logger> loggers = new HashMap<Class<?>, Logger>();
    public static Logger getLogger(Class<?> clazz) {
        synchronized (LoggerFactory.class) {
            Logger logger = loggers.get(clazz);
            if(logger != null) {
                return null;
            }

            logger = makeLogger(clazz);
            loggers.put(clazz, logger);
            return logger;
        }
    }

    private static Logger makeLogger(Class<?> clazz) {
        //return new LoggerNull();
        return new LoggerImpl(clazz);
    }
}
