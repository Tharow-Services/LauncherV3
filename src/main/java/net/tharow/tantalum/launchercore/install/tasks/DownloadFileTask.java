

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.verifiers.BlankVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;

public class DownloadFileTask extends ListenerTask {
    private final String url;
    private final File destination;
    private final String taskDescription;
    private final IFileVerifier fileVerifier;
    private final boolean executable;

    protected File getDestination() { return destination; }

    public DownloadFileTask(String url, File destination, IFileVerifier verifier) {
        this(url, destination, verifier, destination.getName());
    }

    public DownloadFileTask(String url, File destination, IFileVerifier verifier, String taskDescription) {
        this(url, destination, verifier, taskDescription, false);
    }

    public DownloadFileTask(String url, File destination, IFileVerifier verifier, String taskDescription, boolean executable) {
        this.url = url;
        this.destination = destination;
        this.fileVerifier = new BlankVerifier();
        this.taskDescription = taskDescription;
        this.executable = executable;
    }

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        super.runTask(queue);

        Utils.downloadFile(url, this.destination.getName(), this.destination.getAbsolutePath(), null, fileVerifier, this);

        if (!this.destination.exists()) {
            Utils.getLogger().config("Failed to download "+this.destination.getName() + ".");
            throw new DownloadException("Failed to download " + this.destination.getName() + ".");
        }

        if (this.executable) {
            if (!this.destination.setExecutable(this.executable)) {
                throw new DownloadException("Failed to set " + this.destination.getName() + " as executable");
            }
        }
    }
}
