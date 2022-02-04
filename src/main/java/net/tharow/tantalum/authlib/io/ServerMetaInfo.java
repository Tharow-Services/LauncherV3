package net.tharow.tantalum.authlib.io;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused"})
public class ServerMetaInfo {
    private String serverName = "Hidden";
    private String implementationName = "Unknown Implementation";
    private String implementationVersion = "Unknown Impl Version";
    private Links links;
    @SerializedName("feature.non_email_login") private boolean isEmailLogin = false;
    @SerializedName("feature.legacy_skin_api") private boolean isLegacySkinApi = false;
    @SerializedName("feature.no_mojang_namespace") private boolean isMojangNamespace = false;

    public ServerMetaInfo(){}

    public ServerMetaInfo(final String name, final String impl, final String version){
        this.serverName = name;
        this.implementationName = impl;
        this.implementationVersion = version;
    }

    public ServerMetaInfo(final String serverName, final String implementationName, final String implementationVersion, final Links links){
        this.serverName =serverName;
        this.implementationName = implementationName;
        this.implementationVersion = implementationVersion;
        this.links = links;

    }

    public ServerMetaInfo(final String serverName, final String implementationName, final String implementationVersion, final String homepage,
                          final String registerPage, final boolean isEmailLogin,final boolean isLegacySkinApi, final boolean isMojangNamespace){
        this.serverName = serverName;
        this.implementationName = implementationName;
        this.implementationVersion = implementationVersion;
        this.links = new Links(homepage,registerPage);
        this.isEmailLogin = isEmailLogin;
        this.isLegacySkinApi = isLegacySkinApi;
        this.isMojangNamespace = isMojangNamespace;

    }

    public String getServerName() {
        return this.serverName;
    }
    public String getImplName() {
        return this.implementationName;
    }
    public String getImplVersion() {
        return this.implementationVersion;
    }

    public String getHomepage(){return links.getHomepage();}

    public String getRegister(){return links.getRegister();}

    public boolean isEmailLogin(){
        return !this.isEmailLogin;
    }

    public boolean isLegacySkinApi() {
        return this.isLegacySkinApi;
    }

    public boolean isMojangNamespace() {return !this.isMojangNamespace;}
    
    // TODO: 12/19/2021 ADD Other Meta Info And Meta Checks


    @Override
    public String toString() {
        return "ServerMetaInfo{" +
                "serverName='" + serverName + '\'' +
                ", implementationName='" + implementationName + '\'' +
                ", implementationVersion='" + implementationVersion + '\'' +
                ", links=" + links +
                ", isEmailLogin=" + isEmailLogin +
                ", isLegacySkinApi=" + isLegacySkinApi +
                ", isMojangNamespace=" + isMojangNamespace +
                '}';
    }

}