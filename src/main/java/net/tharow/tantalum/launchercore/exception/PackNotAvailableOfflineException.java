

package net.tharow.tantalum.launchercore.exception;

import java.io.IOException;

public class PackNotAvailableOfflineException extends IOException {
    private final String packDisplayName;
    private Throwable cause;
    private static final long serialVersionUID = 3246491999503435492L;

    public PackNotAvailableOfflineException(String displayName) {
        this.packDisplayName = displayName;
    }

    public PackNotAvailableOfflineException(String displayName, Throwable cause) {
        this.packDisplayName = displayName;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "The modpack " + packDisplayName + " does not appear to be installed or is corrupt, and is not available for Offline Play.";
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
