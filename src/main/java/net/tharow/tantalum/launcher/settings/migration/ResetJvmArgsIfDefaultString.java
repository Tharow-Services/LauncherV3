/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2021 Syndicate, LLC
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

package net.tharow.tantalum.launcher.settings.migration;

import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.tantalum.Tantalum;

import java.util.LinkedList;
import java.util.List;

public class ResetJvmArgsIfDefaultString implements IMigrator {
    @Override
    public String getMigrationVersion() {
        return "1";
    }

    @Override
    public String getMigratedVersion() {
        return "2";
    }

    @Override
    public void migrate(TantalumSettings settings, IInstalledPackRepository packStore, LauncherDirectories directories, IUserStore users) {
        // If the JVM flags are set to the default string we set it to null/empty string so we can change the defaults
        // in the future.

        // Null check
        if (settings.getJavaArgs() == null) {
            return;
        }

        if (settings.getJavaArgs().equalsIgnoreCase("-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M")
                || settings.getJavaArgs().equalsIgnoreCase(TantalumSettings.DEFAULT_JAVA_ARGS)) {
            settings.setJavaArgs(null);
        }
    }
}