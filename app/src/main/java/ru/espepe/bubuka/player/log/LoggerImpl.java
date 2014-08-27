package ru.espepe.bubuka.player.log;

import android.util.Log;

/**
 * Created by wolong on 23/06/14.
 */
public class LoggerImpl implements Logger {
    private final Class<?> clazz;

    public LoggerImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    private static enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final String TAG = "BUBUKA";

    private void log(LogLevel level, String msg, Object[] args) {

        //exception.
        //Log.i(TAG, String.format())
        /*
        if(exception != null) {
            Object[] args2 = new Object[args.length+1];
            System.arraycopy(args, 0, args2, 0, args.length);
            args2[args.length] = exception;
            args = args2;
        }
        */

        Log.i(TAG, MessageFormatter.arrayFormat("[" + level.name() + "] " + msg, args));
    }

    @Override
    public void trace(String msg) {
        log(LogLevel.TRACE, msg, null);
    }

    @Override
    public void trace(String format, Object arg) {
        log(LogLevel.TRACE, format, new Object[]{arg});
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log(LogLevel.TRACE, format, new Object[] {arg1, arg2});
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(LogLevel.TRACE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log(LogLevel.TRACE, msg, new Object[] {t});
    }

    @Override
    public void debug(String msg) {
        log(LogLevel.DEBUG, msg, null);
    }

    @Override
    public void debug(String format, Object arg) {
        log(LogLevel.DEBUG, format, new Object[]{arg});
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log(LogLevel.DEBUG, format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(LogLevel.DEBUG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(LogLevel.DEBUG, msg, new Object[] {t});
    }

    @Override
    public void info(String msg) {
        log(LogLevel.INFO, msg, null);
    }

    @Override
    public void info(String format, Object arg) {
        log(LogLevel.INFO, format, new Object[]{arg});
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log(LogLevel.INFO, format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(String format, Object... arguments) {
        log(LogLevel.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(LogLevel.INFO, msg, new Object[] {t});
    }

    @Override
    public void warn(String msg) {
        log(LogLevel.WARN, msg, null);
    }

    @Override
    public void warn(String format, Object arg) {
        log(LogLevel.WARN, format, new Object[]{arg});
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(LogLevel.WARN, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log(LogLevel.WARN, format, new Object[]{arg1, arg2});
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(LogLevel.WARN, msg, new Object[]{t});
    }

    @Override
    public void error(String msg) {
        log(LogLevel.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object arg) {
        log(LogLevel.ERROR, format, new Object[]{arg});
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log(LogLevel.ERROR, format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(String format, Object... arguments) {
        log(LogLevel.ERROR, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(LogLevel.ERROR, msg, new Object[]{t});
    }
}
