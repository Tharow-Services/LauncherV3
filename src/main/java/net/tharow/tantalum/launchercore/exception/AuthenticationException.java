

package net.tharow.tantalum.launchercore.exception;

public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 5887385045789342851L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super (message, cause);
    }
}
