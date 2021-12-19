package net.tharow.tantalum.authlib;

import net.tharow.tantalum.launchercore.auth.IUserType;

public interface IAuthlibServerApi {

    public IAuthlibServerInfo getServerInfo();

    public AuthlibAuthenticator getAuthenticator();

}
