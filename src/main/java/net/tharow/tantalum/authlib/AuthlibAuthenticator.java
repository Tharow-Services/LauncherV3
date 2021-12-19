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

import com.google.common.base.Charsets;
import net.tharow.tantalum.launchercore.exception.AuthenticationException;
import net.tharow.tantalum.launchercore.exception.ResponseException;
import net.tharow.tantalum.launchercore.exception.SessionException;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.auth.request.AuthRequest;
import net.tharow.tantalum.minecraftcore.mojang.auth.request.RefreshRequest;
import net.tharow.tantalum.minecraftcore.mojang.auth.response.AuthResponse;
import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthlibAuthenticator {
    private final String clientToken;
    //TODO Implement non email login


    public AuthlibAuthenticator(String clientToken){
        this.clientToken = clientToken;
    }


    public AuthlibUser loginNewUser(String username, String password, IAuthlibServerInfo authlibServerInfo) throws AuthenticationException {
        AuthRequest request = new AuthRequest(username, password, this.clientToken);
        String data = MojangUtils.getGson().toJson(request);
        String authServer = authlibServerInfo.getServerUrl();
        AuthResponse response;
        try {
            String returned = postJson(authServer + "authserver/authenticate", data);
            response = MojangUtils.getGson().fromJson(returned, AuthResponse.class);
            if (response == null) {
                throw new ResponseException("Auth Error", "Invalid credentials. Invalid username or password.");
            }
            if (response.hasError()) {
                throw new ResponseException(response.getError(), response.getErrorMessage());
            }
        } catch (ResponseException e) {
            throw e;
        }  catch (IOException e) {
            throw new AuthenticationException(
                    "An error was raised while attempting to communicate with " + authServer + ".", e);
        }

        return new AuthlibUser(username, response, authlibServerInfo);
    }

    public AuthResponse requestRefresh(AuthlibUser authlibUser) throws AuthenticationException {
        RefreshRequest refreshRequest = new RefreshRequest(authlibUser.getAccessToken(), authlibUser.getClientToken());
        String data = MojangUtils.getGson().toJson(refreshRequest);
        String authServer = authlibUser.getServerUrl();


        AuthResponse response;
        try {
            String returned = postJson(authServer + "authserver/refresh", data);
            response = MojangUtils.getGson().fromJson(returned, AuthResponse.class);
            if (response == null) {
                throw new SessionException("Session Error. Try logging in again.");
            }
            if (response.hasError()) {
                throw new ResponseException(response.getError(), response.getErrorMessage());
            }
        } catch (IOException e) {
            throw new AuthenticationException(
                    "An error was raised while attempting to communicate with " + authServer + ".", e);
        }

        return response;
    }

    private String postJson(String url, String data) throws IOException {
        byte[] rawData = data.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(rawData.length));
        connection.setRequestProperty("Content-Language", "en-US");

        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(rawData);
        writer.flush();
        writer.close();

        InputStream stream = null;
        String returnable = null;
        try {
            stream = connection.getInputStream();
            returnable = IOUtils.toString(stream, Charsets.UTF_8);
        } catch (IOException e) {
            stream = connection.getErrorStream();

            if (stream == null) {
                throw e;
            }
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException ignored) {}
        }

        return returnable;
    }

    public AuthlibUser createOfflineUser(String displayName) {
        return new AuthlibUser(displayName);
    }
}
