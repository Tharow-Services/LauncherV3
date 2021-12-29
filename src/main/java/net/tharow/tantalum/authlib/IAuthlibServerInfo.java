package net.tharow.tantalum.authlib;

import java.util.List;

public interface IAuthlibServerInfo {

    String getServerUrl();

    void setServerUrl(String serverUrl);

    String getServerName();

    String getImplName();

    String getImplVersion();

    String getHomepage();

    String getRegisterPage();

    boolean isEmailLogin();

    boolean isLegacySkinApi();

    boolean isMojangNamespace();

    List<String> getSkinDomains();

    String getPublicKey();

    String toReadable();
}
