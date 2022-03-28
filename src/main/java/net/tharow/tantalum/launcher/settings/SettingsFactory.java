

package net.tharow.tantalum.launcher.settings;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launcher.settings.migration.IMigrator;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class SettingsFactory {
    public static TantalumSettings buildSettingsObject(String runningDir, boolean isMover) {

        System.out.println("Settings for exe: "+runningDir);

        File portableSettingsDir = getPortableSettingsDir(runningDir, isMover);

        if (portableSettingsDir == null)
            System.out.println("Portable settings dir has broken terribly");
        else
            System.out.println("Portable settings dir: "+portableSettingsDir.getAbsolutePath());

        TantalumSettings portableSettings = tryGetSettings(portableSettingsDir);

        if (portableSettings != null && portableSettings.isPortable()) {
            System.out.println("Portable settings file found.");
            return portableSettings;
        }

        File installedSettingsDir = OperatingSystem.getOperatingSystem().getUserDirectoryForApp("tantalum");

        return tryGetSettings(installedSettingsDir);
    }

    public static void migrateSettings(TantalumSettings settings, IInstalledPackRepository packStore, LauncherDirectories directories, IUserStore users, List<IMigrator> migrators) {
        for(IMigrator migrator : migrators) {
            String version = settings.getLauncherSettingsVersion();
            boolean bothNull = version == null && migrator.getMigrationVersion() == null;
            if (bothNull || (version != null && version.equals(migrator.getMigrationVersion())))  {
                migrator.migrate(settings, packStore, directories, users);
                settings.setLauncherSettingsVersion(migrator.getMigratedVersion());
            }
        }

        settings.save();
    }

    private static TantalumSettings tryGetSettings(File rootDir) {
        if (!rootDir.exists())
            return null;

        File settingsFile = new File(rootDir, "settings.json");
        if (!settingsFile.exists())
            return null;

        try {
            String json = FileUtils.readFileToString(settingsFile, StandardCharsets.UTF_8);
            TantalumSettings settings = Utils.getGson().fromJson(json, TantalumSettings.class);

            if (settings != null)
                settings.setFilePath(settingsFile);

            return settings;
        } catch (JsonSyntaxException | IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load version from " + settingsFile);
            return null;
        }
    }
    @Deprecated
    private static File getPortableSettingsDir(String runningDir, boolean isMover) {
        return new File(runningDir).getParentFile();
        //Legacy//
        /*
        if (isMover)
            return runningFolder;
        else
            return new File(runningFolder,"tantalum");
        */
    }
}
