

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.util.DownloadListener;

import java.io.IOException;

public abstract class ListenerTask implements IInstallTask, DownloadListener {

    private float taskProgress;
    private InstallTasksQueue queue;

    public ListenerTask() {
        taskProgress = 0;
    }

    @Override
    public float getTaskProgress() {
        return this.taskProgress;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        this.queue = queue;
    }

    protected void setQueue(InstallTasksQueue queue) {
        this.queue = queue;
    }

    public void stateChanged(String fileName, float progress) {
        this.taskProgress = progress;
        this.queue.refreshProgress();
    }
}
