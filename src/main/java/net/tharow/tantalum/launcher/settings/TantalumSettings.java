/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2015 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launcher.settings;

import net.tharow.tantalum.launchercore.util.LaunchAction;
import net.tharow.tantalum.minecraftcore.launch.ILaunchOptions;
import net.tharow.tantalum.minecraftcore.launch.WindowType;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;

public class TantalumSettings implements ILaunchOptions {
    public static final String STABLE = "stable";
    public static final String BETA = "beta";

    private transient File settingsFile;
    private transient File technicRoot;
    private int memory;
    private LaunchAction launchAction = LaunchAction.HIDE;
    private String buildStream = STABLE;
    private boolean showConsole = false;
    private String languageCode = "default";
    private String clientId = UUID.randomUUID().toString();
    private String directory;
    private String javaArgs;
    private String wrapperCommand;
    private int latestNewsArticle;
    private boolean launchToModpacks = true;
    private String javaVersion = "default";
    private boolean autoAcceptRequirements = false;
    /**
     * 64 bit if true, 32 bit if false
     */
    private boolean javaBitness = true;

    private String launcherSettingsVersion = "1";

    private WindowType windowType = WindowType.DEFAULT;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private boolean enableStencilBuffer = true;
    private boolean useMojangJava = true;
    private boolean useCustomDNS = false;
    private boolean useSocksProxy = false;
    private boolean useHTTPProxy = false;
    private boolean useTorRelay = false;
    private String socksProxyHost = "localhost";
    private int socksProxyPort = 3652;
    private boolean useSocksProxyFive = true; //If false
    private String HTTPProxyHost = "localhost";
    private int HTTPProxyPort = 3652;
    private String HTTPProxyBypassDomains = "";
    private String authlibServerURL = ""; //Authlib-Injector Server Address
    private String discoverURL = "https://check.torproject.org";
    private String solderURL = "https://tantalum-solder.azurewebsites.net/api/";
    private String platformURL = "https://tantalum-auth.azurewebsites.net/";
    private Boolean advOptions = false;

    public boolean getAdvOptions(){return this.advOptions;}
    public void setAdvOptions(boolean advOptions){this.advOptions = advOptions;}

    //Website Urls
    public String getAuthlibServerURL(){
        if(authlibServerURL.equals("")){
            return platformURL;
        } else {return  this.authlibServerURL;}
    }
    public void setAuthlibServerURL(String authlibServerURL){this.authlibServerURL = authlibServerURL;}

    public String getDiscoverURL(){return this.discoverURL;}
    public void setDiscoverURL(String discoverURL){this.discoverURL = discoverURL;}

    public String getSolderURL(){return this.solderURL;}
    public void setSolderURL(String solderURL){this.solderURL = solderURL;}

    public String getPlatformURL(){return this.platformURL;}
    public void setPlatformURL(String platformURL){this.platformURL = platformURL;}

    //Socks Proxy Configs
    public boolean getUseSocksProxy(){return this.useSocksProxy;}
    public void setUseSocksProxy(boolean useSocksProxy){this.useSocksProxy = useSocksProxy;}

    public String getSocksProxyHost(){ return this.socksProxyHost;}
    public void setSocksProxyHost(String proxyHost){this.socksProxyHost = proxyHost;}

    public int getSocksProxyPort(){ return this.socksProxyPort;}
    public void setSocksProxyPort(int proxyPort){this.socksProxyPort = proxyPort;}

    public boolean getUseSocksProxyFive(){return this.useSocksProxyFive;}
    public void setUseSocksProxyFive(boolean useSocksProxyFive){this.useSocksProxyFive = useSocksProxyFive;}
    //HTTP Proxy Config
    public boolean getUseHTTPProxy(){return this.useHTTPProxy;}
    public void setUseHTTPProxy(boolean useHTTPProxy){this.useHTTPProxy = useHTTPProxy;}
    public String getHTTPProxyHost(){ return this.HTTPProxyHost;}
    public void setHTTPProxyHost(String proxyHost){this.HTTPProxyHost = proxyHost;}

    public int getHTTPProxyPort(){ return this.HTTPProxyPort;}
    public void setHTTPProxyPort(int proxyPort){this.HTTPProxyPort = proxyPort;}

    public String getHTTPProxyBypassDomains(){ return this.HTTPProxyBypassDomains;}
    public void setHTTPProxyBypassDomains(String HTTPProxyBypassDomains){this.HTTPProxyBypassDomains = HTTPProxyBypassDomains;}
    //Tor Relay Config
    public boolean getUseTorRelay(){return this.useTorRelay;}
    public void setUseTorRelay(boolean useTorRelay){this.useTorRelay = useTorRelay;}

    //Custom DNS Config
    public boolean getUseDNSOnly(){ return  this.useCustomDNS;}
    public void setUseDNSOnly(boolean useCustomDNS){this.useCustomDNS = useCustomDNS;}

    //Other Launcher Settings
    public File getFilePath() { return this.settingsFile; }
    public void setFilePath(File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public File getTechnicRoot() {
        if (technicRoot == null || !technicRoot.exists())
            buildTechnicRoot();

        return technicRoot;
    }

    public String getLauncherSettingsVersion() { return launcherSettingsVersion; }
    public void setLauncherSettingsVersion(String version) { this.launcherSettingsVersion = version; }

    public boolean isPortable() {
        return (directory != null && !directory.isEmpty() && directory.equalsIgnoreCase("portable"));
    }

    public void setPortable() {
        directory = "portable";
    }

    public void installTo(String directory) {
        this.directory = directory;
    }

    public int getMemory() { return memory; }
    public void setMemory(int memory) {
        this.memory = memory;
        save();
    }

    public LaunchAction getLaunchAction() { return launchAction; }
    public void setLaunchAction(LaunchAction launchAction) {
        this.launchAction = launchAction;
        save();
    }

    public String getBuildStream() { return buildStream; }
    public void setBuildStream(String buildStream) {
        this.buildStream = buildStream;
        save();
    }

    public String getJavaVersion() { return javaVersion; }
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
        save();
    }

    public boolean getJavaBitness() { return javaBitness; }
    public void setJavaBitness(boolean javaBitness) {
        this.javaBitness = javaBitness;
        save();
    }

    public boolean getShowConsole() { return showConsole; }
    public void setShowConsole(boolean showConsole) {
        this.showConsole = showConsole;
        save();
    }

    //Whether to launch into the modpacks tab directly or launch to the discover tab
    public boolean getLaunchToModpacks() { return launchToModpacks; }
    public void setLaunchToModpacks(boolean launchToModpacks) {
        this.launchToModpacks = launchToModpacks;
        save();
    }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        save();
    }

    public int getLatestNewsArticle() { return latestNewsArticle; }
    public void setLatestNewsArticle(int latestNewsArticle)
    {
        this.latestNewsArticle = latestNewsArticle;
        save();
    }

    public boolean shouldAutoAcceptModpackRequirements() { return autoAcceptRequirements; }
    public void setAutoAcceptModpackRequirements(boolean value) {
        this.autoAcceptRequirements = value;
        save();
    }

    public WindowType getLaunchWindowType() { return windowType; }
    public void setLaunchWindowType(WindowType type) {
        this.windowType = type;
        save();
    }

    public int getCustomWidth() { return windowWidth; }
    public int getCustomHeight() { return windowHeight; }
    public void setLaunchWindowDimensions(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        save();
    }

    public boolean shouldUseStencilBuffer() { return enableStencilBuffer; }
    public void setUseStencilBuffer(boolean stencilBuffer) {
        this.enableStencilBuffer = stencilBuffer;
        save();
    }

    public String getClientId() { return clientId; }

    public String getJavaArgs() {
        if (javaArgs == null || javaArgs.isEmpty()) {
            // These are the default JVM args in the vanilla launcher
            javaArgs = "-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M -javaagent:" + this.getTechnicRoot().getAbsolutePath() + "\\assets\\launcher\\authlib-injector.jar=" + this.authlibServerURL;
        }
        return javaArgs;
    }
    public void setJavaArgs(String args) { javaArgs = args; }

    public String getWrapperCommand() {
        return wrapperCommand;
    }

    public void setWrapperCommand(String wrapperCommand) {
        this.wrapperCommand = wrapperCommand;
    }

    public boolean shouldUseMojangJava() {
        return useMojangJava;
    }

    public void setUseMojangJava(boolean useMojangJava) {
        this.useMojangJava = useMojangJava;
    }

    public void save() {
        String json = Utils.getGson().toJson(this);

        try {
            FileUtils.writeStringToFile(settingsFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save installed " + settingsFile);
        }
    }

    protected void buildTechnicRoot() {
        if (directory == null || directory.isEmpty() || directory.equalsIgnoreCase("portable"))
            technicRoot = settingsFile.getParentFile();
        else
            technicRoot = new File(directory);

        if (!technicRoot.exists())
            technicRoot.mkdirs();
    }
}
