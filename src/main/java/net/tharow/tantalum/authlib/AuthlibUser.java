/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.authlib;

import net.tharow.tantalum.authlib.io.ServerInfo;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.auth.UserModel;
import net.tharow.tantalum.launchercore.exception.AuthenticationException;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.auth.io.Profile;
import net.tharow.tantalum.minecraftcore.mojang.auth.io.UserProperties;
import net.tharow.tantalum.minecraftcore.mojang.auth.response.AuthResponse;

@SuppressWarnings("unused")
public class AuthlibUser implements IUserType {
    public static final String AUTHLIB_USER_TYPE = "authlib";
    private static final String NON_EMAIL_LOGIN = "nonEmailLogin";

    private String username;
    private String accessToken;
    private String clientToken;
    private String displayName;
    private Profile profile;
    private UserProperties userProperties;
    private String authServer;
    private ServerInfo serverInfo;
    private final transient boolean isOffline;

    public AuthlibUser() {
        this.isOffline = false;
    }

    //This constructor is used to build a user for offline mode
    public AuthlibUser(String username) {
        this.username = username;
        this.displayName = username;
        this.accessToken = "0";
        this.clientToken = "0";
        this.profile = new Profile("0", "");
        this.isOffline = true;
        this.userProperties = new UserProperties();
    }

    public AuthlibUser(String username, AuthResponse response, IAuthlibServerInfo serverInfo) {
        this.isOffline = false;
        this.username = username;
        this.serverInfo = (ServerInfo) serverInfo;
        this.authServer = serverInfo.getServerUrl();
        refreshToken(response);
    }

    private void refreshToken(AuthResponse response) {
        this.accessToken = response.getAccessToken();
        this.clientToken = response.getClientToken();
        this.displayName = response.getSelectedProfile().getName();
        this.profile = response.getSelectedProfile();

        if (response.getUser() == null) {
            this.userProperties = new UserProperties();
        } else {
            this.userProperties = response.getUser().getUserProperties();
        }
    }

    @Override
    public void login(UserModel userModel) throws AuthenticationException {
        refreshToken(userModel.getAuthlibAuthenticator().requestRefresh(this));
    }

    @Override
    public String getUserType() {
        return AUTHLIB_USER_TYPE;
    }

    public String getId() {
        return profile.getId();
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Profile getProfile() {
        return profile;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public String getSessionId() {
        return "token:" + accessToken + ":" + profile.getId();
    }

    public String getMCUserType() {
        return getProfile().isLegacy() ? NON_EMAIL_LOGIN : AUTHLIB_USER_TYPE;
    }

    public String getUserProperties() {
        if (this.userProperties != null)
            return MojangUtils.getUglyGson().toJson(this.userProperties);
        else
            return "{}";
    }

    public UserProperties properties() {
        return userProperties;
    }

    public void mergeUserProperties(AuthlibUser mergeUser) {
        if (this.userProperties != null && mergeUser.userProperties != null)
            this.userProperties.merge(mergeUser.userProperties);
    }

    public IAuthlibServerInfo getAuthlibServerInfo(){
        this.serverInfo.setServerUrl(this.authServer);
        return this.serverInfo;
    }
    @Override
    public String getServerUrl(){
        return this.authServer;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
