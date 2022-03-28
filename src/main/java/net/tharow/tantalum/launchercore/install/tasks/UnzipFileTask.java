

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.utilslib.IZipFileFilter;
import net.tharow.tantalum.utilslib.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

public class UnzipFileTask extends ListenerTask {
    private final File zipFile;
    private final File destination;
    private final IZipFileFilter filter;

    public UnzipFileTask(File zipFile, File destination, IZipFileFilter filter) {
        this.zipFile = zipFile;
        this.destination = destination;
        this.filter = filter;
    }

    @Override
    public String getTaskDescription() {
        return "Unzipping " + this.zipFile.getName();
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        super.runTask(queue);

        if (!zipFile.exists()) {
            throw new ZipException("Attempting to extract file " + zipFile.getName() + ", but it did not exist.");
        }

        if (!destination.exists()) {
            destination.mkdirs();
        }

        try {
            ZipUtils.unzipFile(zipFile, destination, filter, this);
        } catch (ZipException ex) {
            ex.printStackTrace();
            throw new ZipException("Error extracting file "+zipFile.getName()+".");
        }
    }
}
