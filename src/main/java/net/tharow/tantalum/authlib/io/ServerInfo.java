package net.tharow.tantalum.authlib.io;

import net.tharow.tantalum.authlib.IAuthlibServerInfo;
import net.tharow.tantalum.rest.RestObject;

import java.util.List;
@SuppressWarnings({"unused"})
public class ServerInfo extends RestObject implements IAuthlibServerInfo {

    private ServerMetaInfo meta;
    private List<String> skinDomains;
    private String signaturePublicKey;
    private transient String serverUrl;

    @Override
    public String getServerUrl() {
        return this.serverUrl;
    }

    @Override
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public String getServerName() {
        return this.meta.getServerName();
    }

    @Override
    public String getImplName() {
        return this.meta.getImplName();
    }

    @Override
    public String getImplVersion() {
        return this.meta.getImplVersion();
    }

    @Override
    public String getHomepage() {
        return this.meta.getHomepage();
    }

    @Override
    public String getRegisterPage() {
        return this.meta.getRegister();
    }

    @Override
    public boolean isEmailLogin() {
        return this.meta.isEmailLogin();
    }

    @Override
    public boolean isLegacySkinApi() {
        return this.meta.isLegacySkinApi();
    }

    @Override
    public boolean isMojangNamespace() {
        return this.meta.isMojangNamespace();
    }

    @Override
    public List<String> getSkinDomains() {
        return this.skinDomains;
    }

    @Override
    public String getPublicKey() {
        return this.signaturePublicKey;
    }

    @Override
    public String toString(){
        return "Authlib{" +
                "meta={" +
                "serverName='" + this.meta.getServerName() + '\'' +
                ", implementationName='" + this.meta.getImplName() + '\'' +
                ", implementationVersion='" + this.meta.getImplName() + '\'' +", " +
                "links={"+
                "homepage='" + this.meta.getHomepage() + '\''+", "+
                "register='" + this.meta.getRegister() + '\''+" }, "+
                "feature.non_email_login=" + !this.meta.isEmailLogin() + '\''+ " ," +
                "feature.legacy_skin_api=" + this.meta.isLegacySkinApi() + '\''+ " ," +
                "feature.no_mojang_namespace=" + !this.meta.isMojangNamespace() + '\''+ " } " +
                "skinDomains=[ " + this.skinDomains.toString() + " ], " +
                "signaturePublicKey='" + this.signaturePublicKey + '\'' + "" +
                '}';
    }

    @Override
    public String toReadable(){
        return "Authlib: " + this.serverUrl +
                "\nServer Name: " + this.meta.getServerName() +
                "\nImplementation Name: " + this.meta.getImplName() +
                "\nImplementation Version: " + this.meta.getImplName() +
                "\nHome Page Url: " + this.meta.getHomepage() +
                "\nRegister Page Url: '" + this.meta.getRegister() +
                "\nUses Email Login: " + this.meta.isEmailLogin() +
                "\nUses Legacy Skin Api: " + this.meta.isLegacySkinApi() +
                "\nUses Mojang Name Space: " + this.meta.isMojangNamespace() +
                "\nSkin Domains: " + this.skinDomains.toString() +
                "\nContains Public Key: " + this.signaturePublicKey;
    }

}
