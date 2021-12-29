package net.tharow.tantalum.minecraftcore.microsoft.auth.model;

import com.google.api.client.util.Key;
import net.tharow.tantalum.launchercore.exception.MicrosoftAuthException;

/**
 *
 */
public class XboxMinecraftRequest {
    @Key(value="identityToken") private final String identityToken;

    public XboxMinecraftRequest(XboxResponse xstsResponse) throws MicrosoftAuthException {
        identityToken = "XBL3.0 x=" + xstsResponse.getUserhash() + ";" + xstsResponse.token;
    }
}
