package net.tharow.tantalum.tantalum;

import java.io.IOException;

public class RequiresAccessCode extends IOException {
    private static final long serialVersionUID = 1L;

    private final Throwable cause;
    private final String message;

    public RequiresAccessCode(String message, Throwable cause) {
        this.cause = cause;
        this.message = message;
    }

    public RequiresAccessCode(Throwable cause) {
        this(null, cause);
    }

    public RequiresAccessCode(String message) {
        this(message, null);
    }

    public RequiresAccessCode() {
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
