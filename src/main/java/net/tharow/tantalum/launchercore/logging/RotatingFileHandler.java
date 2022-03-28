

package net.tharow.tantalum.launchercore.logging;

import net.tharow.tantalum.utilslib.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class RotatingFileHandler extends StreamHandler {
    private final SimpleDateFormat dateFormat;
    private final String logPathFormat;
    private String currentLogPath;

    public RotatingFileHandler(String logPathFormat) {
        this.logPathFormat = logPathFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentLogPath = calculatePath();
        try {
            setOutputStream(new FileOutputStream(currentLogPath, true));
        } catch (FileNotFoundException ex) {
            Utils.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private String calculatePath() {
        return logPathFormat.replace("%D", dateFormat.format(new Date()));
    }

    private void changeFileIfNeeded() {
        final String newPath = calculatePath();
        if (!currentLogPath.equals(newPath)) {
            final String oldPath = currentLogPath;
            currentLogPath = newPath;
            try {
                this.close();
                setOutputStream(new FileOutputStream(currentLogPath, true));
                super.publish(new LogRecord(Level.INFO, "Continued from " + oldPath));
            } catch (FileNotFoundException ex) {
                Utils.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public synchronized void publish(LogRecord record) {
        changeFileIfNeeded();
        super.publish(record);
        flush();
    }
}