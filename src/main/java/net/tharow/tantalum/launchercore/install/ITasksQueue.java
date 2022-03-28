

package net.tharow.tantalum.launchercore.install;

import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

public interface ITasksQueue {
    void addNextTask(IInstallTask task);

    void addTask(IInstallTask task);
}
