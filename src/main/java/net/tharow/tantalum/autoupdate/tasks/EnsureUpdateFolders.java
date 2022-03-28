

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

import java.io.File;

public class EnsureUpdateFolders implements IInstallTask {
    private final String taskDescription;
    private final LauncherDirectories directories;

    public EnsureUpdateFolders(String taskDescription, LauncherDirectories directories) {
        this.taskDescription = taskDescription;
        this.directories = directories;
    }

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) {
        File launcherAssets = new File(directories.getAssetsDirectory(), "launcher");
        File patches = new File(launcherAssets, "patches");

        if (!patches.exists())
            patches.mkdirs();
    }
}
