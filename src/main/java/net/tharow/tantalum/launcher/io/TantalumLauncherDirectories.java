

package net.tharow.tantalum.launcher.io;

import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TantalumLauncherDirectories implements LauncherDirectories {
    private final File workDir;

    public TantalumLauncherDirectories(File rootDir) {
        workDir = rootDir;
    }

    public File getLauncherDirectory() {return getDirectoryBase(null);}

    public File getCacheDirectory() {return getDirectoryBase("cache");}
    public File getAssetsDirectory() {return getDirectoryBase("assets");}
    public File getModpacksDirectory() {return getDirectoryBase("modpacks");}
    public File getRuntimesDirectory() {return getDirectoryBase("runtimes");}
    public File getWorldsDirectory() {return getDirectoryBase("worlds");}
    public File getLogsDirectory() {return  getDirectoryBase("logs");}
    public File getPacksCache() {return getDirectoryBase(getAssetsDirectory(),"packs");}
    public File getLauncherAssetsDirectory() {return getDirectoryBase(getAssetsDirectory(),"launcher");}


    private @NotNull File getDirectoryBase(@Nullable final String directory){
        if (!workDir.exists()){workDir.mkdir();}
        return directory != null ? getDirectoryBase(workDir, directory) : this.workDir;
    }
    private @NotNull File getDirectoryBase(final File parent, final String child){
        File directory = new File(parent, child);
        if (!directory.exists()){directory.mkdir();}
        return directory;
    }

}