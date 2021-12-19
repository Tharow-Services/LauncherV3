package net.tharow.tantalum.minecraftcore.microsoft.auth;

import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.auth.UserModel;
import net.tharow.tantalum.launchercore.exception.AuthenticationException;
import net.tharow.tantalum.minecraftcore.microsoft.auth.model.MinecraftProfile;
import net.tharow.tantalum.minecraftcore.microsoft.auth.model.XboxMinecraftResponse;

public class MicrosoftUser implements IUserType {
    public static final String MC_MS_USER_TYPE = "msa";

    private String id;
    private String username;
    private String accessToken;

    public MicrosoftUser() {
    }

    public MicrosoftUser(XboxMinecraftResponse authResponse, MinecraftProfile profile) {
        this();
        this.id = profile.id;
        this.username = profile.name;
        updateAuthToken(authResponse);
    }

    @Override
    public String getUserType() {
        return MC_MS_USER_TYPE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getDisplayName() {
        return username;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getSessionId() {
        return "token:" + accessToken + ":" + getId();
    }

    @Override
    public String getMCUserType() {
        return MC_MS_USER_TYPE;
    }

    @Override
    public String getUserProperties() {
        return "{}";
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    @Override
    public void login(UserModel userModel) throws AuthenticationException {
        userModel.getMicrosoftAuthenticator().refreshSession(this);
    }

    @Override
    public String getServerUrl() {
        return null;
    }

    public void updateAuthToken(XboxMinecraftResponse authResponse) {
        this.accessToken = authResponse.accessToken;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
