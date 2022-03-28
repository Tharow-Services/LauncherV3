

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.DownloadFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

import java.io.IOException;
import java.util.Collection;

public class DownloadUpdate extends DownloadFileTask {
    private final Relauncher relauncher;
    private final Collection<IInstallTask> postUpdateActions;

    public DownloadUpdate(String url, Relauncher relauncher, Collection<IInstallTask> postUpdateActions) {
        super(url, relauncher.getTempLauncher(), null, relauncher.getUpdateText());

        this.relauncher = relauncher;
        this.postUpdateActions = postUpdateActions;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        super.runTask(queue);

        if (relauncher.isUpdateOnly() && getDestination().exists()) {
            for (IInstallTask task : postUpdateActions) {
                queue.addTask(task);
            }
        }
        relauncher.setUpdated();
    }
}
