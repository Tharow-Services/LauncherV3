

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CopyFileTask implements IInstallTask {
    private final File source;
    private final File destination;

    public CopyFileTask(File source, File destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String getTaskDescription() {
        return "Copying files.";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        FileUtils.copyFile(this.source, this.destination);
    }
}
