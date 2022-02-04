package net.tharow.tantalum.launchercore.logging;

import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleFormatter extends Formatter {
    /**
     * Format the given log record and return the formatted string.
     * <p>
     * The resulting formatted String will normally include a
     * localized and formatted version of the LogRecord's message field.
     * It is recommended to use the {@link Formatter#formatMessage}
     * convenience method to localize and format the message field.
     *
     * @param record the log record to be formatted.
     * @return the formatted log record
     */
    @Override
    public String format(@NotNull LogRecord record) {
        StringBuilder builder = new StringBuilder();
        if (record.getLoggerName() != null) {
            if (!record.getLoggerName().equals(Utils.getLogger().getName())) {
                if (!record.getSourceClassName().endsWith("Logger")) {
                    builder.append(addInfo(record.getLoggerName())).append(' ');
                    String[] array = record.getSourceClassName().split("\\.");
                    builder.append(addInfo(array[array.length-1])).append(' ');
                    if(record.getSourceMethodName() != null){
                        builder.append(addInfo(record.getSourceMethodName())).append(' ');
                    }
                } else {
                    builder.append(addInfo(record.getLoggerName())).append(' ');
                }

            } else {
                builder.append(addInfo("Launcher")).append(' ');
            }

        } else {
            builder.append(addInfo("System")).append(' ');
        }

        builder.append(formatMessage(record));
        builder.append('\n');

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }

        return builder.toString();
    }
    public String addInfo(String infoString){
        return addInfo(infoString, null, null);
    }
    public String addInfo(String infoString, String startString, String endString){
        final String start = (startString==null)?"[":startString;
        final String end = (endString==null)?"]":endString;
        return start + infoString + end;
    }
}
