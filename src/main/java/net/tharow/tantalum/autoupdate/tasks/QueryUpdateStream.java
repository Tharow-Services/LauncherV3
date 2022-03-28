

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.IUpdateStream;
import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.autoupdate.io.LauncherResource;
import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.install.tasks.DownloadFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.MD5FileVerifier;
import net.tharow.tantalum.rest.RestfulAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class QueryUpdateStream implements IInstallTask {

    private final String description;
    private final ITasksQueue downloadTasks;
    private final IUpdateStream updateStream;
    private final LauncherDirectories directories;
    private final Relauncher relauncher;
    private final Collection<IInstallTask> postDownloadTasks;

    public QueryUpdateStream(String description, IUpdateStream stream, ITasksQueue downloadTasks, LauncherDirectories directories, Relauncher relauncher, Collection<IInstallTask> postDownloadTasks) {
        this.description = description;
        this.downloadTasks = downloadTasks;
        this.updateStream = stream;
        this.directories = directories;
        this.relauncher = relauncher;
        this.postDownloadTasks = postDownloadTasks;
    }

    @Override
    public String getTaskDescription() {
        return description;
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        try {
            StreamVersion version = updateStream.getStreamVersion(relauncher.getStreamName());

            if (version == null || version.getBuild() == 0)
                return;

            for(LauncherResource resource : version.getResources()) {
                IFileVerifier verifier = new MD5FileVerifier(resource.getMd5());
                File downloadFile = new File(new File(directories.getAssetsDirectory(), "launcher"), resource.getFilename());
                if (!downloadFile.exists() || !verifier.isFileValid(downloadFile))
                    downloadTasks.addTask(new DownloadFileTask(resource.getUrl(), downloadFile, verifier, resource.getFilename()));
            }

            if (version.getBuild() == relauncher.getCurrentBuild() || (relauncher.getStreamName().startsWith("beta") && version.getBuild() <= relauncher.getCurrentBuild()))
                return;

            String updateUrl = null;
            String runningPath = relauncher.getRunningPath();

            if (runningPath == null) {
                throw new DownloadException("Could not load a running path for currently-executing launcher.");
            }

            if (runningPath.endsWith(".exe"))
                updateUrl = version.getExeUrl();
            else
                updateUrl = version.getJarUrl();

            downloadTasks.addTask(new DownloadUpdate(updateUrl, relauncher, postDownloadTasks));
        } catch (RestfulAPIException ex) {
            ex.printStackTrace();
        }
    }
}
