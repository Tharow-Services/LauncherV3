

package net.tharow.tantalum.launchercore.auth;

import net.tharow.tantalum.launchercore.exception.AuthenticationException;

import java.util.UUID;

public interface IUserType {
    String getUserType();

    String getId();

    String getUsername();

    String getDisplayName();

    String getAccessToken();

    String getSessionId();

    String getMCUserType();

    String getUserProperties();

    boolean isOffline();

    void login(UserModel userModel) throws AuthenticationException;

    UUID getAuthServerUUID();
}
