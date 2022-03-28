

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

public class LaunchLauncherMode implements IInstallTask {

    private final String description;
    private final Relauncher relauncher;
    private final String launchTarget;
    private final boolean isLegacy;

    public LaunchLauncherMode(String description, Relauncher relauncher, String launchTarget, boolean isLegacy) {
        this.description = description;
        this.relauncher = relauncher;
        this.launchTarget = launchTarget;
        this.isLegacy = isLegacy;
    }

    @Override
    public String getTaskDescription() {
        return description;
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) {
        String[] args = relauncher.buildLauncherArgs(isLegacy);
        relauncher.launch(launchTarget, args);
    }
}
