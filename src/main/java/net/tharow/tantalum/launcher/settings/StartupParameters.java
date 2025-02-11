

package net.tharow.tantalum.launcher.settings;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.internal.Lists;
import lombok.Getter;

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
    @Parameter(names = {"-discover","-d"}, description = "Change Discover Url")
    private String discover = null;
    @Parameter(names = {"-overrideRoots","-orca"}, description = "Force the override of root ca Certificates")
    private boolean overrideRoots = false;
    @Parameter(names = {"-file","-f"}, description = "Install a modpack from file", converter = FileConverter.class)
    private File inputFile = null;
    @Parameter(names = {"-uri","-u"}, description = "Install a modpack from api link")
    private String inputUri = null;
    @Parameter(names = {"-blockReboot"}, description = "Prevent rebooting the launcher due to bad java properties.")
    private boolean blockReboot = false;
    @Parameter(names = {"-buildNumber","-b"}, description = "Force build number to this value for debugging.")
    private String buildNumber = "";
    @Parameter(names = {"-offline"}, description = "Force offline mode")
    private boolean offline = false;
    @Parameter(names = {"-setting"})
    @Getter private String setting;
    @Parameter(names = {"-corca"})
    private boolean changeOverride = false;
    @Parameter(names = {"-unlock"}, hidden = true)
    @Getter private boolean unlocked = false;

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
        return solder;
    }

    public String getDiscover(){
        return discover;
    }

    public String getBuildNumber() { return buildNumber; }

    public List<String> getPlatformUrl() {
        return platform;
    }

    public boolean isOverrideRoots() {return overrideRoots;}

    public File getInputFile() {return inputFile;}

    public String getInputUri() {return inputUri;}

    public int getLogLevel() {
        return logLevel;
    }


    public boolean isChangeOverride() {
        return changeOverride;
    }
}