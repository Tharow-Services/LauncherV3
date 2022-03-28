

package net.tharow.tantalum.launcher.settings.migration;

import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;

public interface IMigrator {
    String getMigrationVersion();
    String getMigratedVersion();
    void migrate(TantalumSettings settings, IInstalledPackRepository packStore, LauncherDirectories directories, IUserStore users);
}
