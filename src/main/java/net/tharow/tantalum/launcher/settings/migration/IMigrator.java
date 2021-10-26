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

package net.tharow.tantalum.launcher.settings.migration;

import net.tharow.tantalum.launcher.settings.TechnicSettings;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;

public interface IMigrator {
    String getMigrationVersion();
    String getMigratedVersion();
    void migrate(TechnicSettings settings, IInstalledPackRepository packStore, LauncherDirectories directories, IUserStore users);
}
