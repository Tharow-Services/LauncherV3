package net.tharow.tantalum.minecraftcore.install.tasks;

import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.install.tasks.EnsureFileTask;
import net.tharow.tantalum.launchercore.install.tasks.ListenerTask;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.MD5FileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.SHA1FileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.ValidZipFileVerifier;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.minecraftcore.tharow.version.ExtractRulesFileFilter;
import net.tharow.tantalum.minecraftcore.tharow.version.io.Library;
import net.tharow.tantalum.utilslib.*;


import java.io.File;
import java.io.IOException;

public class InstallVersionLibTask extends ListenerTask {
    private Library library;
    private ITasksQueue grabQueue;
    private ITasksQueue downloadLibraryQueue;
    private ITasksQueue copyLibraryQueue;
    private ModpackModel pack;
    private LauncherDirectories directories;

    public InstallVersionLibTask(Library library, ITasksQueue grabQueue, ITasksQueue downloadLibraryQueue, ITasksQueue copyLibraryQueue, ModpackModel pack, LauncherDirectories directories) {
        this.library = library;
        this.downloadLibraryQueue = downloadLibraryQueue;
        this.copyLibraryQueue = copyLibraryQueue;
        this.grabQueue = grabQueue;
        this.pack = pack;
        this.directories = directories;
    }

    @Override
    public String getTaskDescription() {
        return library.getName();
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        super.runTask(queue);

        queue.refreshProgress();

        // Native classifier as in the library's downloads -> classifiers -> $nativeClassifier
        // (the mapping of which is taken from the library's natives map)
        String nativeClassifier = null;
        File extractDirectory = null;
        if (library.getNatives() != null) {
            nativeClassifier = library.getNatives().get(OperatingSystem.getOperatingSystem());

            if (nativeClassifier != null) {
                extractDirectory = new File(this.pack.getBinDir(), "natives");
            }
        }

        String path = library.getArtifactPath(nativeClassifier).replace("${arch}", JavaUtils.getJavaBitness());

        File cache = new File(directories.getCacheDirectory(), path);
        if (cache.getParentFile() != null) {
            cache.getParentFile().mkdirs();
        }

        IFileVerifier verifier;

        String sha1 = library.getArtifactSha1(nativeClassifier);
        if (sha1 != null && !sha1.isEmpty())
            verifier = new SHA1FileVerifier(sha1);
        else
            verifier = new ValidZipFileVerifier();

        // TODO: Add check based on size (so it fails early if the size is different)
        if (cache.exists() && verifier.isFileValid(cache) && extractDirectory == null)
            return;

        String url = null;

        // TODO: this causes verification to happen twice, for natives
        if (!cache.exists() || !verifier.isFileValid(cache)) {
            url = library.getDownloadUrl(path).replace("${arch}", JavaUtils.getJavaBitness());
            if (sha1 == null || sha1.isEmpty()) {
                String md5 = Utils.getETag(url);
                if (md5 != null && !md5.isEmpty()) {
                    verifier = new MD5FileVerifier(md5);
                }
            }
        }

        IZipFileFilter filter = null;

        if (library.getExtract() != null)
            filter = new ExtractRulesFileFilter(library.getExtract());

        grabQueue.addTask(new EnsureFileTask(cache, verifier, extractDirectory, url, downloadLibraryQueue, copyLibraryQueue, filter));
    }
}
