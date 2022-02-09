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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.internal.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("ALL")
public final class StartupParameters {
    private final String[] args;
    @Parameter
    private List<String> parameters = Lists.newArrayList();
    @Parameter(names = {"-console","-c"}, description = "Shows the console window")
    private boolean console = false;
    @Parameter(names = {"-logLevel","-ll"}, description = "Sets Log Level")
    private int logLevel = 8;
    @Parameter(names = {"-launcheronly","-lo"}, description = "Starts in launcher mode (rather than update/mover)")
    private boolean launcher = false;
    @Parameter(names = {"-launcher"}, description = "Legacy launcher mode (indicates we should do a full update)")
    private boolean oldLauncher = false;
    @Parameter(names = {"-moveronly"}, description = "Starts in mover mode (copies recently-downloaded update to originally-run package)")
    private boolean mover = false;
    @Parameter(names = {"-mover"}, description = "Legacy mover mode- used to detect old-updater clients trying to update")
    private boolean oldMover = false;
    @Parameter(names = {"-update"}, description = "Starts in update mode (closes after downloading updated resources)")
    private boolean update = false;
    @Parameter(names = {"-movetarget"}, description = "The path of the originally-run package to copy to")
    private String moveTarget = null;
    @Parameter(names = {"-solder","-s"}, description = "An override param for the discover URL")
    private String solder = null;
    @Parameter(names = {"-platform","-p"}, description = "Add A Platform To the list")
    private List<String> platform = new ArrayList<>();
    @Parameter(names = {"-overrideRoots","-orca"}, description = "Force the override of root ca Certificates")
    private boolean overrideRoots = false;
    @Parameter(names = {"-modpackFile","-mf"}, description = "Install a modpack from file", converter = FileConverter.class)
    private File modpackFile = null;
    @Parameter(names = {"-modpackUri","-mu"}, description = "Install a modpack from api link")
    private String modpackUri = null;
    @Parameter(names = {"-blockReboot"}, description = "Prevent rebooting the launcher due to bad java properties.")
    private boolean blockReboot = false;
    @Parameter(names = {"-buildNumber","-b"}, description = "Force build number to this value for debugging.")
    private String buildNumber = "";
    @Parameter(names = {"-offline"}, description = "Force offline mode")
    private boolean offline = false;
    @Parameter(names = {"-technic","-t"}, description = "Start In Technic Launcher Mode")
    private boolean isTechnic = false;

    public StartupParameters(String[] args) {
        this.args = args;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String[] getArgs() { return args; }

    public boolean isConsole() {
        return console;
    }

    public boolean isLauncher() { return launcher; }

    public boolean isLegacyLauncher() { return oldLauncher; }

    public boolean isMover() { return mover; }

    public boolean isLegacyMover() { return oldMover; }

    public boolean isUpdate() { return update; }

    public boolean isBlockReboot() { return blockReboot; }

    public boolean isOffline() {return offline;}

    public String getMoveTarget() { return moveTarget; }

    public String getSolderUrl() {
        if(isTechnic){
            return "https://solder.technicpack.net/api/";
        }else
            return solder;
    }

    public String getBuildNumber() { return buildNumber; }

    public List<String> getPlatformUrl() {
        return platform;
    }

    public boolean isOverrideRoots() {return overrideRoots;}

    public File getModpackFile() {return modpackFile;}

    public String getModpackUri() {return modpackUri;}

    public int getLogLevel() {
        return logLevel;
    }
}