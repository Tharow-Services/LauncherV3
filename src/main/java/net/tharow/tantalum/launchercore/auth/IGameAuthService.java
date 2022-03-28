

package net.tharow.tantalum.launchercore.auth;

public interface IGameAuthService<UserData> {
    UserData createClearedUser(String username, IAuthResponse response);

    UserData createOfflineUser(String displayName);

    IAuthResponse requestRefresh(UserData user);

    IAuthResponse requestLogin(String username, String password, String data);
}
