

package net.tharow.tantalum.launchercore.install;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.utilslib.Utils;

import java.io.IOException;

public class ModpackInstaller<VersionData> {

    private final IPlatformApi platformApi;
    private final String clientId;
    public ModpackInstaller (final IPlatformApi platformApi, final String clientId){
        this.platformApi = platformApi;
        this.clientId = clientId;
        }

    public VersionData installPack(InstallTasksQueue<VersionData> tasksQueue, ModpackModel modpack, String build) throws IOException, InterruptedException {
        modpack.save();
        modpack.initDirectories();

        Version installedVersion = modpack.getInstalledVersion();
        tasksQueue.runAllTasks();

        Version versionFile = new Version(build, false);
        versionFile.save(modpack.getBinDir());

        if (installedVersion == null) {
            platformApi.incrementPackInstalls(modpack.getName());
            Utils.sendTracking("installModpack", modpack.getName(), modpack.getBuild(), clientId);
        }

        return tasksQueue.getMetadata();
    }
}
