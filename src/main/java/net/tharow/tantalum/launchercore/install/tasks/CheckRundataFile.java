package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Modpack;

import java.io.File;

public class CheckRundataFile implements IInstallTask {
    private final ModpackModel modpackModel;
    private final Modpack modpack;
    private final TaskGroup writeRunDataGroup;

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
    public void runTask(InstallTasksQueue queue) {
        File file = modpackModel.getBinDir();
        File runDataFile = new File(file, "runData");

        if (runDataFile.exists())
            return;
        if (modpackModel.isLocalOnly())
            return;

        writeRunDataGroup.addTask(new WriteRundataFile(modpackModel, modpack));
    }
}
