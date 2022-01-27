package net.tharow.tantalum.utilslib.logger;

public class Level extends java.util.logging.Level {
    /**
     * Create a named Level with a given integer value.
     * <p>
     * Note that this constructor is "protected" to allow subclassing.
     * In general clients of logging should use one of the constant Level
     * objects such as SEVERE or FINEST.  However, if clients need to
     * add new logging levels, they may subclass Level and define new
     * constants.
     *
     */
    public static final java.util.logging.Level DEBUG = new Level("DEBUG",600);
    public static final java.util.logging.Level STACK = new Level("STACK",200); // Not Sure if These Will Ever be used
    public static final java.util.logging.Level FINE_STACK = new Level("FINE_STACK",100);
    protected Level(String name, int value) {
        super(name, value);
    }

    public static java.util.logging.Level getLevel(int levelInt) {
        switch (levelInt){
            case 1000 : return SEVERE;
            case 900 : return WARNING;
            //case 800 : return INFO;
            case 700 : return CONFIG;
            case 600 : return DEBUG;
            case 500 : return FINE;
            case 400 : return FINER;
            case 300 : return FINEST;
            case 200 : return STACK;
            case 100 : return FINE_STACK;
            default: return INFO;

        }
    }

}
