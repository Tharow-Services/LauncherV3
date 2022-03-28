

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.utilslib.IZipFileFilter;

import java.io.File;

public class EnsureFileTask<TaskQueue extends ITasksQueue> implements IInstallTask {
    private final File cacheLocation;
    private final File zipExtractLocation;
    private final String sourceUrl;
    private final String friendlyFileName;
    private final IFileVerifier fileVerifier;
    private final TaskQueue downloadTaskQueue;
    private final TaskQueue copyTaskQueue;
    private final IZipFileFilter filter;
    private boolean executable;

    public EnsureFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, TaskQueue downloadTaskQueue, TaskQueue copyTaskQueue) {
        this(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, fileLocation.getName(), downloadTaskQueue, copyTaskQueue, null);
    }

    public EnsureFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, TaskQueue downloadTaskQueue, TaskQueue copyTaskQueue, IZipFileFilter filter) {
        this(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, fileLocation.getName(), downloadTaskQueue, copyTaskQueue, filter);
    }

    public EnsureFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, String friendlyFileName, TaskQueue downloadTaskQueue, TaskQueue copyTaskQueue) {
        this(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, friendlyFileName, downloadTaskQueue, copyTaskQueue, null);
    }

    public EnsureFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, String friendlyFileName, TaskQueue downloadTaskQueue, TaskQueue copyTaskQueue, IZipFileFilter fileFilter) {
        this.cacheLocation = fileLocation;
        this.zipExtractLocation = zipExtractLocation;
        this.sourceUrl = sourceUrl;
        this.fileVerifier = fileVerifier;
        this.friendlyFileName = friendlyFileName;
        this.downloadTaskQueue = downloadTaskQueue;
        this.copyTaskQueue = copyTaskQueue;
        this.filter = fileFilter;
    }

    @Override
    public String getTaskDescription() {
        return "Verifying " + this.cacheLocation.getName();
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) {
        if (this.zipExtractLocation != null)
            unzipFile(this.copyTaskQueue, this.cacheLocation, this.zipExtractLocation, this.filter);

        if (sourceUrl != null && (!this.cacheLocation.exists() || (fileVerifier != null && !fileVerifier.isFileValid(this.cacheLocation))))
            downloadFile(this.downloadTaskQueue, this.sourceUrl, this.cacheLocation, this.fileVerifier, this.friendlyFileName);
    }

    public void unzipFile(TaskQueue taskQueue, File zipLocation, File targetLocation, IZipFileFilter filter) {
        taskQueue.addNextTask(new UnzipFileTask(zipLocation, targetLocation, filter));
    }

    public void downloadFile(TaskQueue taskQueue, String sourceUrl, File targetLocation, IFileVerifier verifier, String fileName) {
        taskQueue.addNextTask(new DownloadFileTask(sourceUrl, targetLocation, verifier, fileName, this.executable));
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }
}
