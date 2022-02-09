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

import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.NewsArticle;
import net.tharow.tantalum.rest.RestfulAPIException;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class InitialV3Migrator implements IMigrator {

    public InitialV3Migrator() {}

    @Override
    public String getMigrationVersion() {
        return "0";
    }

    @Override
    public String getMigratedVersion() {
        return "1";
    }

    @Override
    public void migrate(TantalumSettings settings, @NotNull IInstalledPackRepository packStore, LauncherDirectories directories, IUserStore users) {
        //A fresh install/upgrade from v2 shouldn't show the latest news as being new

        List<ModpackModel> deletePacks = new LinkedList<>();
        for (String packName : packStore.getPackNames()) {
            InstalledPack pack = packStore.getInstalledPacks().get(packName);
            ModpackModel model = new ModpackModel(pack, null, packStore, directories);

            if (!model.getInstalledDirectory().exists()) {
                deletePacks.add(model);
            }
        }

        for (ModpackModel deletePack : deletePacks) {
            deletePack.delete();
        }

        packStore.save();
    }
}
