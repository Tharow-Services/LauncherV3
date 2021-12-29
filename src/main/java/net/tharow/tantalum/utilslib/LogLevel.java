package net.tharow.tantalum.utilslib;

import java.util.logging.Level;

public class LogLevel extends Level {
    /**
     * Create a named Level with a given integer value.
     * <p>
     * Note that this constructor is "protected" to allow subclassing.
     * In general clients of logging should use one of the constant Level
     * objects such as SEVERE or FINEST.  However, if clients need to
     * add new logging levels, they may subclass Level and define new
     * constants.
     *
     * @param name  the name of the Level, for example "SEVERE".
     * @param value an integer value for the level.
     * @throws NullPointerException if the name is null
     */
    public static final Level DEBUG = new LogLevel("DEBUG",600);
    public static final Level STACK = new LogLevel("STACK",200); // Not Sure if These Will Ever be used
    public static final Level FINE_STACK = new LogLevel("FINE_STACK",100);
    protected LogLevel(String name, int value) {
        super(name, value);
    }

    public static Level getLevel(int levelInt) {
        Level result = INFO;
        switch (levelInt){
            case 1000 -> result = SEVERE;
            case 900 -> result = WARNING;
            //case 800 -> result = INFO;
            case 700 -> result = CONFIG;
            case 600 -> result = DEBUG;
            case 500 -> result = FINE;
            case 400 -> result = FINER;
            case 300 -> result = FINEST;
            case 200 -> result = STACK;
            case 100 -> result = FINE_STACK;

        }
        return result;
    }

}
