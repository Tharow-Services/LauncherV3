package net.tharow.tantalum.authlib.response;

import net.tharow.tantalum.launchercore.auth.IAuthResponse;

public class Response implements IAuthResponse {
    private String error;
    private String errorMessage;
    private String cause;

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCause() {
        return cause;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "Response{" +
                "error='" + error + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", cause='" + cause + '\'' +
                '}';
    }
}

