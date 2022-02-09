package net.tharow.tantalum.utilslib;

public class DisabledUntilFutureVersion extends Exception
{
    private static final long serialVersionUID = 1L;

    private final Throwable cause;
    private final String message;

    public DisabledUntilFutureVersion(String message, Throwable cause) {
        this.cause = cause;
        this.message = message;
    }

    public DisabledUntilFutureVersion(Throwable cause) {
        this(null, cause);
    }

    public DisabledUntilFutureVersion(String message) {
        this(message, null);
    }

    public DisabledUntilFutureVersion() {
        this(null, null);
    }

    @Override
    public synchronized Throwable getCause() {
        return this.cause;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
