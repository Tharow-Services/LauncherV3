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

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.Mod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CleanupModpackCacheTask implements IInstallTask {
	private final ModpackModel pack;
	private final Modpack modpack;

	public CleanupModpackCacheTask(ModpackModel pack, Modpack modpack) {
		this.pack = pack;
		this.modpack = modpack;
	}

	@Override
	public String getTaskDescription() {
		return "Cleaning Modpack Cache";
	}

	@Override
	public float getTaskProgress() {
		return 0;
	}

	@Override
	public void runTask(InstallTasksQueue queue) {
		File[] files = this.pack.getCacheDir().listFiles();

		if (files == null) {
			return;
		}

		Set<String> keepFiles = new HashSet<>(modpack.getMods().size() + 1);
		for (Mod mod : modpack.getMods()) {
			String version = mod.getVersion();

			String name;
			if (version != null) {
				name = mod.getName() + "-" + version + ".zip";
			} else {
				name = mod.getName() + ".zip";
			}

			keepFiles.add(name);
		}
		keepFiles.add("minecraft.jar");

		for (File file : files) {
			String fileName = file.getName();
			if (keepFiles.contains(fileName)) {
				continue;
			}
			FileUtils.deleteQuietly(file);
		}
	}
}
