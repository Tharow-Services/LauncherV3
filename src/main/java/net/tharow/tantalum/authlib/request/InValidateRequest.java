package net.tharow.tantalum.authlib.request;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class InValidateRequest {
    private final String accessToken, clientToken;

    public InValidateRequest(final String accessToken, final String clientToken){
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
}
