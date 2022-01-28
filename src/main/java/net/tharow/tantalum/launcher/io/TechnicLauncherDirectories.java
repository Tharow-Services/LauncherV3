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

package net.tharow.tantalum.launcher.io;

import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TechnicLauncherDirectories implements LauncherDirectories {
    private final File workDir;

    public TechnicLauncherDirectories(File rootDir) {
        workDir = rootDir;
    }

    public File getLauncherDirectory() {return getDirectoryBase(null);}

    public File getCacheDirectory() {return getDirectoryBase("cache");}
    public File getAssetsDirectory() {return getDirectoryBase("assets");}
    public File getModpacksDirectory() {return getDirectoryBase("modpacks");}
    public File getRuntimesDirectory() {return getDirectoryBase("runtimes");}
    public File getWorldsDirectory() {return getDirectoryBase("worlds");}
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