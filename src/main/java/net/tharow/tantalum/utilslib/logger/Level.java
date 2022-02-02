package net.tharow.tantalum.utilslib.logger;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

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
    public static final Level DEBUG = new Level("DEBUG",600);
    public static final Level STACK = new Level("STACK",200); // Not Sure if These Will Ever be used
    public static final Level FINE_STACK = new Level("FINE_STACK",100);
    public static final Level ENTERING = new Level("ENTERING", 401);
    public static final Level EXITING = new Level("EXITING",402);
    public static SimpleAttributeSet HIGHLIGHTED_ATTRIBUTE_SET, ERROR_ATTRIBUTE_SET, WARNING_ATTRIBUTE_SET, INFO_ATTRIBUTE_SET, DEBUG_ATTRIBUTE_SET,FINE_ATTRIBUTE_SET,FINER_ATTRIBUTE_SET,FINEST_ATTRIBUTE_SET,DEFAULT_ATTRIBUTE_SET = new SimpleAttributeSet();

    static {
        HIGHLIGHTED_ATTRIBUTE_SET = new SimpleAttributeSet();
        StyleConstants.setForeground(HIGHLIGHTED_ATTRIBUTE_SET, Color.BLACK);
        StyleConstants.setBackground(HIGHLIGHTED_ATTRIBUTE_SET, Color.YELLOW);

        ERROR_ATTRIBUTE_SET = new SimpleAttributeSet();
        StyleConstants.setForeground(ERROR_ATTRIBUTE_SET, Color.RED);

        WARNING_ATTRIBUTE_SET = new SimpleAttributeSet();
        StyleConstants.setForeground(WARNING_ATTRIBUTE_SET, Color.YELLOW);

        DEBUG_ATTRIBUTE_SET


    }

    protected Level(String name, int value, SimpleAttributeSet set, Color fore, Color back){
        super(name, value);
        StyleConstants.setForeground(set, fore);
        StyleConstants.setBackground(set, back);
    }

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
            case 401 : return ENTERING;
            case 402 : return EXITING;
            case 300 : return FINEST;
            case 200 : return STACK;
            case 100 : return FINE_STACK;
            default: return INFO;

        }
    }

    public static SimpleAttributeSet getAttributeSet(Level level){
        switch (level){
            case SEVERE: return new SimpleAttributeSet()
        }
    }

}
