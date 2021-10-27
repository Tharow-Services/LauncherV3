package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Modpack;

import java.io.File;
import java.io.IOException;

public class CheckRundataFile implements IInstallTask {
    private ModpackModel modpackModel;
    private Modpack modpack;
    private TaskGroup writeRunDataGroup;

    public CheckRundataFile(ModpackModel modpackModel, Modpack modpack, TaskGroup writeRunDataGroup) {
        this.modpackModel = modpackModel;
        this.modpack = modpack;
        this.writeRunDataGroup = writeRunDataGroup;
    }

    @Override
    public String getTaskDescription() {
        return "Checking Runtime Data...";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        File file = modpackModel.getBinDir();
        File runDataFile = new File(file, "runData");

        if (runDataFile.exists())
            return;
        if (modpackModel.isLocalOnly())
            return;

        writeRunDataGroup.addTask(new WriteRundataFile(modpackModel, modpack));
    }
}
