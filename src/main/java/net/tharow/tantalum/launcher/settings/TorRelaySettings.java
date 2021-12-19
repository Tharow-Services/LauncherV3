package net.tharow.tantalum.launcher.settings;

import com.msopentech.thali.toronionproxy.TorSettings;

import java.util.List;

public class TorRelaySettings implements TorSettings {
    @Override
    public boolean disableNetwork() {
        return false;
    }

    @Override
    public String dnsPort() {
        return null;
    }

    @Override
    public String getCustomTorrc() {
        return null;
    }

    @Override
    public String getEntryNodes() {
        return null;
    }

    @Override
    public String getExcludeNodes() {
        return null;
    }

    @Override
    public String getExitNodes() {
        return null;
    }

    @Override
    public int getHttpTunnelPort() {
        return 0;
    }

    @Override
    public List<String> getListOfSupportedBridges() {
        return null;
    }

    @Override
    public String getProxyHost() {
        return null;
    }

    @Override
    public String getProxyPassword() {
        return null;
    }

    @Override
    public String getProxyPort() {
        return null;
    }

    @Override
    public String getProxySocks5Host() {
        return null;
    }

    @Override
    public String getProxySocks5ServerPort() {
        return null;
    }

    @Override
    public String getProxyType() {
        return null;
    }

    @Override
    public String getProxyUser() {
        return null;
    }

    @Override
    public String getReachableAddressPorts() {
        return null;
    }

    @Override
    public String getRelayNickname() {
        return null;
    }

    @Override
    public int getRelayPort() {
        return 0;
    }

    @Override
    public String getSocksPort() {
        return null;
    }

    @Override
    public String getVirtualAddressNetwork() {
        return null;
    }

    @Override
    public boolean hasBridges() {
        return false;
    }

    @Override
    public boolean hasConnectionPadding() {
        return false;
    }

    @Override
    public boolean hasCookieAuthentication() {
        return false;
    }

    @Override
    public boolean hasDebugLogs() {
        return false;
    }

    @Override
    public boolean hasDormantCanceledByStartup() {
        return false;
    }

    @Override
    public boolean hasIsolationAddressFlagForTunnel() {
        return false;
    }

    @Override
    public boolean hasOpenProxyOnAllInterfaces() {
        return false;
    }

    @Override
    public boolean hasReachableAddress() {
        return false;
    }

    @Override
    public boolean hasReducedConnectionPadding() {
        return false;
    }

    @Override
    public boolean hasSafeSocks() {
        return false;
    }

    @Override
    public boolean hasStrictNodes() {
        return false;
    }

    @Override
    public boolean hasTestSocks() {
        return false;
    }

    @Override
    public boolean isAutomapHostsOnResolve() {
        return false;
    }

    @Override
    public boolean isRelay() {
        return false;
    }

    @Override
    public boolean runAsDaemon() {
        return false;
    }

    @Override
    public String transPort() {
        return null;
    }

    @Override
    public boolean useSocks5() {
        return false;
    }
}
