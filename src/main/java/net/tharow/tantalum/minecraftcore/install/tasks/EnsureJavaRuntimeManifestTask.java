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

import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.DownloadFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.SHA1FileVerifier;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.java.JavaRuntime;
import net.tharow.tantalum.minecraftcore.mojang.java.JavaRuntimes;
import net.tharow.tantalum.minecraftcore.mojang.version.MojangVersion;
import net.tharow.tantalum.minecraftcore.mojang.version.io.Download;
import net.tharow.tantalum.minecraftcore.mojang.version.io.JavaVersion;

import java.io.File;
import java.io.IOException;

public class EnsureJavaRuntimeManifestTask implements IInstallTask {

    private final File runtimesDirectory;
    private final ModpackModel modpack;
    private final ITasksQueue examineJavaQueue;
    private final ITasksQueue downloadJavaQueue;

    public EnsureJavaRuntimeManifestTask(File runtimesDirectory, ModpackModel modpack, ITasksQueue examineJavaQueue, ITasksQueue downloadJavaQueue) {
        this.runtimesDirectory = runtimesDirectory;
        this.modpack = modpack;
        this.examineJavaQueue = examineJavaQueue;
        this.downloadJavaQueue = downloadJavaQueue;
    }

    @Override
    public String getTaskDescription() {
        return "Retrieving Java runtime manifest";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        MojangVersion version = ((InstallTasksQueue<MojangVersion>)queue).getMetadata();

        JavaVersion wantedRuntime = version.getJavaVersion();

        if (wantedRuntime == null) {
            // Nothing to do here, this version doesn't have a Mojang JRE
            return;
        }

        final String runtimeName = wantedRuntime.getComponent();

        JavaRuntimes availableRuntimes = MojangUtils.getJavaRuntimes();

        if (availableRuntimes == null) {
            throw new DownloadException("Failed to get Mojang JRE information");
        }

        JavaRuntime runtime = availableRuntimes.getRuntimeForCurrentOS(runtimeName);

        Download manifest = runtime.getManifest();

        File output = new File(runtimesDirectory + File.separator + "manifests", runtimeName + ".json");

        (new File(output.getParent())).mkdirs();

        IFileVerifier fileVerifier = new SHA1FileVerifier(manifest.getSha1());

        if (!output.exists() || !fileVerifier.isFileValid(output)) {
            examineJavaQueue.addTask(new DownloadFileTask(manifest.getUrl(), output, fileVerifier));
        }

        examineJavaQueue.addTask(new InstallJavaRuntimeTask(modpack, runtimesDirectory, output, runtimeName, examineJavaQueue, downloadJavaQueue));
    }

}
