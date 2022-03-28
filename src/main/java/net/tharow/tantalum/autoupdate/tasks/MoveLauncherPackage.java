

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class MoveLauncherPackage implements IInstallTask {
    private final String description;
    private final File launcher;
    private final Relauncher relauncher;

    public MoveLauncherPackage(String description, File launcher, Relauncher relauncher) {
        this.description = description;
        this.launcher = launcher;
        this.relauncher = relauncher;
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
        String currentPath = relauncher.getRunningPath();
        Utils.getLogger().log(Level.INFO, "Moving running package from " + currentPath + " to " + launcher.getAbsolutePath());

        File source = new File(currentPath);
        File dest = new File(launcher.getAbsolutePath());

        if (!source.equals(dest)) {
            if (dest.exists()) {
                if (!dest.delete())
                    Utils.getLogger().log(Level.SEVERE, "Deletion of existing package failed!");
            }

            try {
                if (!dest.getParentFile().exists())
                    dest.getParentFile().mkdirs();
                dest.createNewFile();
                try (FileInputStream sourceStream = new FileInputStream(source);
                     FileOutputStream destStream = new FileOutputStream(dest)) {
                    IOUtils.copy(sourceStream, destStream);
                }
            } catch (IOException ex) {
                Utils.getLogger().log(Level.SEVERE, "Error attempting to copy download package:", ex);
            }
        }

        dest.setExecutable(true, true);
    }
}
