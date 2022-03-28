

package net.tharow.tantalum.launchercore.auth;

import net.tharow.tantalum.authlib.Authlib;
import net.tharow.tantalum.authlib.AuthlibAuthenticator;
import net.tharow.tantalum.launchercore.exception.AuthenticationException;
import net.tharow.tantalum.launchercore.exception.ResponseException;
import net.tharow.tantalum.launchercore.exception.SessionException;
import net.tharow.tantalum.minecraftcore.microsoft.auth.MicrosoftAuthenticator;
import net.tharow.tantalum.minecraftcore.mojang.auth.MojangAuthenticator;
import net.tharow.tantalum.rest.RestfulAPIException;

import javax.swing.JOptionPane;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UserModel {
    private IUserType mCurrentUser;
    private final List<IAuthListener> mAuthListeners = new LinkedList<>();
    private final IUserStore mUserStore;
    private final MojangAuthenticator mojangAuthenticator;
    private final MicrosoftAuthenticator microsoftAuthenticator;
    private final Authlib authlib;

    public UserModel(IUserStore userStore, MojangAuthenticator mojangAuthenticator) {
        this.mCurrentUser = null;
        this.authlib = null;
        this.mUserStore = userStore;
        this.mojangAuthenticator = mojangAuthenticator;
        this.microsoftAuthenticator = null;
    }

    public UserModel(IUserStore userStore, MicrosoftAuthenticator microsoftAuthenticator, MojangAuthenticator mojangAuthenticator, Authlib authlib) {
        this.mCurrentUser = null;
        this.mUserStore = userStore;
        this.mojangAuthenticator = mojangAuthenticator;
        this.microsoftAuthenticator = microsoftAuthenticator;
        this.authlib = authlib;

    }

    public IUserType getCurrentUser() {
        return this.mCurrentUser;
    }

    public void setCurrentUser(IUserType user) {
        this.mCurrentUser = user;

        if (user != null)
            setLastUser(user);
        this.triggerAuthListeners();
    }

    public void addAuthListener(IAuthListener listener) {
        this.mAuthListeners.add(listener);
    }

    protected void triggerAuthListeners() {
        for (IAuthListener listener : mAuthListeners) {
            listener.userChanged(this.mCurrentUser);
        }
    }

    public void startupAuth() {
        IUserType user = getLastUser();

        if (user == null) {
            setCurrentUser(null);
            return;
        }

        try {
            user.login(this);
            addUser(user);
            setCurrentUser(user);
        } catch (SessionException | ResponseException ex) {
            setCurrentUser(null);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        } catch (AuthenticationException ex) {
            setCurrentUser(mojangAuthenticator.createOfflineUser(user.getDisplayName()));
        }
    }

    public Collection<IUserType> getUsers() {
        return mUserStore.getSavedUsers();
    }

    public IUserType getLastUser() {
        return mUserStore.getUser(mUserStore.getLastUser());
    }

    public IUserType getUser(String username) {
        return mUserStore.getUser(username);
    }

    public void addUser(IUserType user) {
        mUserStore.addUser(user);
    }

    public void removeUser(IUserType user) {
        mUserStore.removeUser(user.getUsername());
    }

    public void setLastUser(IUserType user) {
        mUserStore.setLastUser(user.getUsername());
    }

    public Authlib getAuthlib(){
        return this.authlib;
    }

    public String getAuthlibURL(){
        return this.authlib.loadServer(mCurrentUser.getAuthServerUUID()).getServerUrl();
    }

    public AuthlibAuthenticator getAuthlibAuthenticator(UUID uuid){
        return this.getAuthlib().loadServer(uuid).getAuthenticator();
    }

    public MojangAuthenticator getMojangAuthenticator(){
        return mojangAuthenticator;
    }

    public MicrosoftAuthenticator getMicrosoftAuthenticator() {
        return microsoftAuthenticator;
    }
}
