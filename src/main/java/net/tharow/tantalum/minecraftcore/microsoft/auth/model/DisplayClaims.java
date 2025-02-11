package net.tharow.tantalum.minecraftcore.microsoft.auth.model;

import com.google.api.client.util.Key;
import net.tharow.tantalum.launchercore.exception.MicrosoftAuthException;

import java.util.ArrayList;
import java.util.List;

public class DisplayClaims {
    @Key(value = "xui")
    public final List<XuiObject> xuiObjects = new ArrayList<>();

    public String getUserhash() throws MicrosoftAuthException {
        if (xuiObjects == null || xuiObjects.isEmpty()) {
            throw new MicrosoftAuthException(MicrosoftAuthException.ExceptionType.XBOX, "User hash is null or empty.");
        }
        return xuiObjects.get(0).getUserhash();
    }
}
