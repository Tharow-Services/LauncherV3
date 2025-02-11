package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

import java.io.File;
import java.io.IOException;

public class LaunchMoverMode implements IInstallTask {

    private final String description;
    private final Relauncher relauncher;
    private final File tempLauncher;

    public LaunchMoverMode(String description, File tempLauncher, Relauncher relauncher) {
        this.relauncher = relauncher;
        this.tempLauncher = tempLauncher;
        this.description = description;
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
    public void runTask(InstallTasksQueue queue) throws IOException {
        relauncher.launch(tempLauncher.getAbsolutePath(), relauncher.buildMoverArgs());
    }
}
