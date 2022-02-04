package net.tharow.tantalum.authlib.request;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SignoutRequest {
    private final String username, password;
    public SignoutRequest(final String username, final String password){
        this.username = username;
        this.password = password;
    }
}
