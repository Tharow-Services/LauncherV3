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
    private boolean launchToModpacks;
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
    private boolean useTorRelay = false;
    private boolean useTorProxy = false;
    private String proxyHost = "localhost";
    private int proxyPort = 3652;
    private boolean proxyVersion = true;
    private int torControlPort = 3651;
    private String nameServers = "localhost:3653";
    private String nameDomain = "localhost";

    public String getProxyHost(){ return this.proxyHost;}
    public void setProxyHost(String proxyHost){this.proxyHost = proxyHost;}

    public int getProxyPort(){ return this.proxyPort;}
    public void setProxyPort(int proxyPort){this.proxyPort = proxyPort;}

    public boolean getProxyVersion(){ return this.proxyVersion;}
    public void setProxyVersion(boolean proxyVersion){this.proxyVersion = proxyVersion;}

    public int getTorControlPort(){ return this.torControlPort;}
    public void setTorControlPort(int torControlPort){this.torControlPort = torControlPort;}

    public String getNameServers(){ return this.nameServers;}
    public void setNameServers(String nameServers){this.nameServers = nameServers;}

    public String getNameServiceDomains(){ return this.nameDomain;}
    public void setNameServiceDomains(String nameServiceDomains){this.nameDomain = nameServiceDomains;}

    public boolean getUseTorRelay(){ return this.useTorRelay;}
    public void setUseTorRelay(boolean useRelay){this.useTorRelay = useRelay;}

    public boolean getUseTorProxy(){ return this.useTorProxy;}
    public void setUseTorProxy(boolean useProxy){this.useTorProxy = useProxy;}

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
            javaArgs = "-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M -javaagent:" + this.getTechnicRoot().getAbsolutePath() + "\\assets\\launcher\\authlib-injector.jar=tantalum-auth.azurewebsites.net";
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
