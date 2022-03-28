

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.IWeightedTasksQueue;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.utilslib.IZipFileFilter;

import java.io.File;

public class EnsureSizedFileTask extends EnsureFileTask<IWeightedTasksQueue> {

    private final int fileSize;
    public EnsureSizedFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, IWeightedTasksQueue downloadTaskQueue, IWeightedTasksQueue copyTaskQueue, int fileSize) {
        super(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, downloadTaskQueue, copyTaskQueue);
        this.fileSize = fileSize;
    }

    public EnsureSizedFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, IWeightedTasksQueue downloadTaskQueue, IWeightedTasksQueue copyTaskQueue, IZipFileFilter filter, int fileSize) {
        super(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, downloadTaskQueue, copyTaskQueue, filter);
        this.fileSize = fileSize;
    }

    public EnsureSizedFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, String friendlyFileName, IWeightedTasksQueue downloadTaskQueue, IWeightedTasksQueue copyTaskQueue, int fileSize) {
        super(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, friendlyFileName, downloadTaskQueue, copyTaskQueue);
        this.fileSize = fileSize;
    }

    public EnsureSizedFileTask(File fileLocation, IFileVerifier fileVerifier, File zipExtractLocation, String sourceUrl, String friendlyFileName, IWeightedTasksQueue downloadTaskQueue, IWeightedTasksQueue copyTaskQueue, IZipFileFilter fileFilter, int fileSize) {
        super(fileLocation, fileVerifier, zipExtractLocation, sourceUrl, friendlyFileName, downloadTaskQueue, copyTaskQueue, fileFilter);
        this.fileSize = fileSize;
    }

    @Override
    public void unzipFile(IWeightedTasksQueue taskQueue, File zipLocation, File targetLocation, IZipFileFilter filter) {
        taskQueue.addNextTask(new UnzipFileTask(zipLocation, targetLocation, filter), fileSize);
    }

    @Override
    public void downloadFile(IWeightedTasksQueue taskQueue, String sourceUrl, File targetLocation, IFileVerifier verifier, String fileName) {
        taskQueue.addNextTask(new DownloadFileTask(sourceUrl, targetLocation, verifier, fileName), fileSize);
    }
}
