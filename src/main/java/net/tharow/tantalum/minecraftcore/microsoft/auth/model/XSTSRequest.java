package net.tharow.tantalum.minecraftcore.microsoft.auth.model;

import com.google.api.client.util.Key;

public class XSTSRequest {
    @Key(value="Properties") private final XSTSProperties properties;
    @Key(value="RelyingParty") private final String relyingParty = "rp://api.minecraftservices.com/";
    @Key(value="TokenType") private final String tokenType = "JWT";

    public XSTSRequest(String token) {
        properties = new XSTSProperties(token);
    }
}
