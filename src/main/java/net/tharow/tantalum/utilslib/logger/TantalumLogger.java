package net.tharow.tantalum.utilslib.logger;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.MissingResourceException;
import java.util.function.Supplier;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TantalumLogger extends Logger {
    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    protected TantalumLogger(String name) {
        super(name, null);
    }

    /**
     * Log a LogRecord.
     * <p>
     * All the other logging methods in this class call through
     * this method to actually perform any logging.  Subclasses can
     * override this single method to capture all log activity.
     *
     * @param record the LogRecord to be published
     */
    @Override
    public void log(LogRecord record) {
        super.log(record);
    }

    /**
     * Log a CONFIG message.
     * <p>
     * If the logger is currently enabled for the CONFIG message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */
    @Override
    public void config(String msg) {
        super.config(msg);
    }

    /**
     * Log a CONFIG message, which is only to be constructed if the logging
     * level is such that the message will actually be logged.
     * <p>
     * If the logger is currently enabled for the CONFIG message
     * level then the message is constructed by invoking the provided
     * supplier function and forwarded to all the registered output
     * Handler objects.
     *
     * @param msgSupplier A function, which when called, produces the
     *                    desired log message
     * @since 1.8
     */
    @Override
    public void config(Supplier<String> msgSupplier) {
        super.config(msgSupplier);
    }

    /**
     * Check if a message of the given level would actually be logged
     * by this logger.  This check is based on the Loggers effective level,
     * which may be inherited from its parent.
     *
     * @param level a message logging level
     * @return true if the given message level is currently being logged.
     */
    @Override
    public boolean isLoggable(java.util.logging.Level level) {
        return super.isLoggable(level);
    }



    public void debug(String msg){
        this.log(new LogRecord(Level.DEBUG, msg));
    }

    public void debug(Supplier<String> msgSupplier){
        this.log(Level.DEBUG, msgSupplier);
    }


    public void entering(@NotNull Class src, String sourceMethod){
        super.entering(src.getName(), sourceMethod);
    }

    public void entering(@NotNull Class src, String sourceMethod, Object param1) {
        super.entering(src.getName(), sourceMethod, param1);
    }

    public void entering(@NotNull Class sourceClass, String sourceMethod, Object[] params) {
        super.entering(sourceClass.getName(), sourceMethod, params);
    }

    public void exiting(@NotNull Class sourceClass, String sourceMethod) {
        super.exiting(sourceClass.getName(), sourceMethod);
    }

    public void exiting(@NotNull Class sourceClass, String sourceMethod, Object result) {
        super.exiting(sourceClass.getName(), sourceMethod, result);
    }

    @Contract("!null -> new")
    public static @NotNull TantalumLogger getLogger(String name) {
        return new TantalumLogger(name);
    }

}
