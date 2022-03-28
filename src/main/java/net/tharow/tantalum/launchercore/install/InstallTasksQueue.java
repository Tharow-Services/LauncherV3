

package net.tharow.tantalum.launchercore.install;

import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.util.DownloadListener;

import java.io.IOException;
import java.util.LinkedList;

public class InstallTasksQueue<Metadata> implements ITasksQueue {
    private final DownloadListener listener;
    private final LinkedList<IInstallTask> tasks;
    private IInstallTask currentTask;
    private Metadata metadata;

    public InstallTasksQueue(DownloadListener listener) {
        this.listener = listener;
        this.tasks = new LinkedList<>();
        this.currentTask = null;
    }

    public void refreshProgress() {
        if (listener != null)
            listener.stateChanged(currentTask.getTaskDescription(), currentTask.getTaskProgress());
    }

    public void runAllTasks() throws IOException, InterruptedException {
        while (!tasks.isEmpty()) {
            currentTask = tasks.removeFirst();
            refreshProgress();
            currentTask.runTask(this);
        }
    }

    public void addNextTask(IInstallTask task) {
        tasks.addFirst(task);
    }

    public void addTask(IInstallTask task) {
        tasks.addLast(task);
    }

    public DownloadListener getDownloadListener() {
        return this.listener;
    }

    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public Metadata getMetadata() { return this.metadata; }
}
