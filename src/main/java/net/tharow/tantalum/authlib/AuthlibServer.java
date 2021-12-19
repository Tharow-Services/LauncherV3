package net.tharow.tantalum.authlib;

import net.tharow.tantalum.authlib.io.ServerInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;

public class AuthlibServer implements IAuthlibServerApi{

    private final AuthlibAuthenticator authenticator;

    private final IAuthlibServerInfo serverInfo;

    public AuthlibServer(String srvUrl, String clientToken) throws RestfulAPIException{
        this.serverInfo = RestObject.getRestObject(ServerInfo.class, srvUrl);
        this.authenticator = null; //new AuthlibAuthenticator();
        this.getServerInfo().setServerUrl(srvUrl);

    }
    @Override
    public IAuthlibServerInfo getServerInfo(){
        return this.serverInfo;
    }


    public AuthlibAuthenticator getAuthenticator() {
        return this.authenticator;
    }

    public static IAuthlibServerInfo getAuthlibServerInfo(String srvUrl) throws RestfulAPIException{
        IAuthlibServerInfo serverinfo = RestObject.getRestObject(ServerInfo.class, srvUrl);
        serverinfo.setServerUrl(srvUrl);
        return serverinfo;
    }
}
