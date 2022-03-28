

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EnsureLinkedFileTask implements IInstallTask {
    private final Path link;
    private final Path target;

    /**
     * Ensures that a symbolic link at "link", pointing to "target" exists and is correct.
     * If it doesn't exist, it will be created.
     * @param link Where the symbolic link is
     * @param target Where the symbolic link points to
     */
    public EnsureLinkedFileTask(File link, File target) {
        this.link = link.toPath();
        this.target = target.toPath();
    }

    @Override
    public String getTaskDescription() {
        return "Linking files.";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        if (Files.isSymbolicLink(link) && Files.readSymbolicLink(link).equals(target)) {
            // link is symlink and points to the right place
            return;
        }

        Files.deleteIfExists(link);
        Files.createSymbolicLink(link, target);
    }
}
