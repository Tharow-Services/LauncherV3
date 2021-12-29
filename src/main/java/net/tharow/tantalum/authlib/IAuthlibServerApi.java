package net.tharow.tantalum.authlib;

public interface IAuthlibServerApi {

    IAuthlibServerInfo getServerInfo();

    AuthlibAuthenticator getAuthenticator();

}
