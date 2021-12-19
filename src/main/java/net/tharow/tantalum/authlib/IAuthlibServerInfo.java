package net.tharow.tantalum.authlib;

import java.util.List;

public interface IAuthlibServerInfo {

    String getServerUrl();

    void setServerUrl(String serverUrl);

    public String getServerName();

    public String getImplName();

    public String getImplVersion();

    public String getHomepage();

    public String getRegisterPage();

    public boolean isEmailLogin();

    public boolean isLegacySkinApi();

    public boolean isMojangNamespace();

    public List<String> getSkinDomains();

    public String getPublicKey();

    String toReadable();
}
