

package net.tharow.tantalum.launchercore.install;

import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

public interface IWeightedTasksQueue extends ITasksQueue {
    void addTask(IInstallTask task, float weight);
    void addNextTask(IInstallTask task, float weight);
}
