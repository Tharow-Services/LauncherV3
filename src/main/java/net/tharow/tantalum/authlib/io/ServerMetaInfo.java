package net.tharow.tantalum.authlib.io;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings({"unused"})
public class ServerMetaInfo {
    private String serverName;
    private String implementationName;
    private String implementationVersion;
    private ServerMetaLinksInfo links;
    @SerializedName("feature.non_email_login") private boolean emailLogin = true;
    @SerializedName("feature.legacy_skin_api") private boolean legacySkinApi = false;
    @SerializedName("feature.no_mojang_namespace") private boolean mojangNamespace = true;


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
        return !this.emailLogin;
    }

    public boolean isLegacySkinApi() {
        return this.legacySkinApi;
    }

    public boolean isMojangNamespace() {return !this.mojangNamespace;}
    
    // TODO: 12/19/2021 ADD Other Meta Info And Meta Checks
}