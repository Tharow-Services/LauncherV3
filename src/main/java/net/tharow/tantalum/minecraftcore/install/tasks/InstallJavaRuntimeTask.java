/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.minecraftcore.install.tasks;

import java.util.Objects;

public final class InstallJavaRuntimeTask implements IInstallTask {
    private final ModpackModel modpack;
    private final File runtimesDirectory;
    private final File runtimeManifestFile;
    private final String runtimeName;
    private final ITasksQueue examineJavaQueue;
    private final ITasksQueue downloadJavaQueue;

    InstallJavaRuntimeTask(ModpackModel modpack,
                           File runtimesDirectory, File runtimeManifestFile,
                           String runtimeName,
                           ITasksQueue examineJavaQueue,
                           ITasksQueue downloadJavaQueue) {
        this.modpack = modpack;
        this.runtimesDirectory = runtimesDirectory;
        this.runtimeManifestFile = runtimeManifestFile;
        this.runtimeName = runtimeName;
        this.examineJavaQueue = examineJavaQueue;
        this.downloadJavaQueue = downloadJavaQueue;
    }

    public ModpackModel modpack() {
        return modpack;
    }

    public File runtimesDirectory() {
        return runtimesDirectory;
    }

    public File runtimeManifestFile() {
        return runtimeManifestFile;
    }

    public String runtimeName() {
        return runtimeName;
    }

    public ITasksQueue examineJavaQueue() {
        return examineJavaQueue;
    }

    public ITasksQueue downloadJavaQueue() {
        return downloadJavaQueue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        InstallJavaRuntimeTask that = (InstallJavaRuntimeTask) obj;
        return Objects.equals(this.modpack, that.modpack) &&
                Objects.equals(this.runtimesDirectory, that.runtimesDirectory) &&
                Objects.equals(this.runtimeManifestFile, that.runtimeManifestFile) &&
                Objects.equals(this.runtimeName, that.runtimeName) &&
                Objects.equals(this.examineJavaQueue, that.examineJavaQueue) &&
                Objects.equals(this.downloadJavaQueue, that.downloadJavaQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modpack, runtimesDirectory, runtimeManifestFile, runtimeName, examineJavaQueue, downloadJavaQueue);
    }

    @Override
    public String toString() {
        return "InstallJavaRuntimeTask[" +
                "modpack=" + modpack + ", " +
                "runtimesDirectory=" + runtimesDirectory + ", " +
                "runtimeManifestFile=" + runtimeManifestFile + ", " +
                "runtimeName=" + runtimeName + ", " +
                "examineJavaQueue=" + examineJavaQueue + ", " +
                "downloadJavaQueue=" + downloadJavaQueue + ']';
    }

    @Override
    public String getTaskDescription() {
        return "Installing Java runtime";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        String json = FileUtils.readFileToString(runtimeManifestFile, StandardCharsets.UTF_8);
        JavaRuntimeManifest manifest = MojangUtils.getGson().fromJson(json, JavaRuntimeManifest.class);

        if (manifest == null) {
            throw new DownloadException("The Java runtime manifest is invalid.");
        }

        // TODO: Check runtime isn't downloaded/installed outside of its directory

        // Create the runtime directory if it doesn't exist
        File runtimeRoot = new File(runtimesDirectory, runtimeName);
        runtimeRoot.mkdirs();

        // First, create the dirs
        manifest.getFiles().forEach((path, runtimeFile) -> {
            // We're only interested in the dirs for now
            if (runtimeFile.getType() == JavaRuntimeFileType.DIRECTORY) {
                File dir = new File(runtimeRoot, path);
                dir.mkdirs();
            }
        });

        // Then, download the files
        manifest.getFiles().forEach((path, runtimeFile) -> {
            // We're only interested in the files right now
            if (runtimeFile.getType() == JavaRuntimeFileType.FILE) {
                File target = new File(runtimeRoot, path);

                // Apparently the Mac Java 8 JRE spec doesn't have any directory entries, so we have to create them regardless
                target.getParentFile().mkdirs();

                Download download = runtimeFile.getDownloads().getRaw();

                IFileVerifier verifier = new SHA1FileVerifier(download.getSha1());

                @SuppressWarnings("unchecked") EnsureFileTask ensureFileTask = new EnsureFileTask(target, verifier, null, download.getUrl(), downloadJavaQueue, null);
                ensureFileTask.setExecutable(runtimeFile.isExecutable());

                examineJavaQueue.addTask(ensureFileTask);
            }
        });

        // Then, create the links
        manifest.getFiles().forEach((path, runtimeFile) -> {
            // We're only interested in links right now
            if (runtimeFile.getType() == JavaRuntimeFileType.LINK) {
                File link = new File(runtimeRoot, path);
                File target = new File(link, runtimeFile.getTarget());

                // We add it to the download queue so it runs after all the files exist
                downloadJavaQueue.addTask(new EnsureLinkedFileTask(link, target));
            }
        });
    }
}
