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

import net.tharow.tantalum.launchercore.exception.CacheDeleteException;
import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.EnsureFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.MD5FileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.ValidZipFileVerifier;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.minecraftcore.install.ModpackZipFilter;
import net.tharow.tantalum.rest.io.Mod;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.utilslib.IZipFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CleanupAndExtractModpackTask implements IInstallTask {
	private final ModpackModel pack;
	private final Modpack modpack;
	private final ITasksQueue checkModQueue;
	private final ITasksQueue downloadModQueue;
	private final ITasksQueue copyModQueue;

	public CleanupAndExtractModpackTask(ModpackModel pack,
										Modpack modpack,
										ITasksQueue checkModQueue,
										ITasksQueue downloadModQueue,
										ITasksQueue copyModQueue) {
		this.pack = pack;
		this.modpack = modpack;
		this.checkModQueue = checkModQueue;
		this.downloadModQueue = downloadModQueue;
		this.copyModQueue = copyModQueue;
	}

	public ModpackModel pack() {
		return pack;
	}

	public Modpack modpack() {
		return modpack;
	}

	public ITasksQueue checkModQueue() {
		return checkModQueue;
	}

	public ITasksQueue downloadModQueue() {
		return downloadModQueue;
	}

	public ITasksQueue copyModQueue() {
		return copyModQueue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CleanupAndExtractModpackTask that = (CleanupAndExtractModpackTask) obj;
		return Objects.equals(this.pack, that.pack) &&
				Objects.equals(this.modpack, that.modpack) &&
				Objects.equals(this.checkModQueue, that.checkModQueue) &&
				Objects.equals(this.downloadModQueue, that.downloadModQueue) &&
				Objects.equals(this.copyModQueue, that.copyModQueue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pack, modpack, checkModQueue, downloadModQueue, copyModQueue);
	}

	@Override
	public String toString() {
		return "CleanupAndExtractModpackTask[" +
				"pack=" + pack + ", " +
				"modpack=" + modpack + ", " +
				"checkModQueue=" + checkModQueue + ", " +
				"downloadModQueue=" + downloadModQueue + ", " +
				"copyModQueue=" + copyModQueue + ']';
	}

	@Override
	public String getTaskDescription() {
		return "Wiping Folders";
	}

	@Override
	public float getTaskProgress() {
		return 0;
	}

	@Override
	public void runTask(InstallTasksQueue queue) throws IOException {
		File modsDir = this.pack.getModsDir();

		if (modsDir != null && modsDir.exists()) {
			deleteMods(modsDir);
		}

		File coremodsDir = this.pack.getCoremodsDir();

		if (coremodsDir != null && coremodsDir.exists()) {
			deleteMods(coremodsDir);
		}

		//HACK - jamioflan is a big jerk who needs to put his mods in the dang mod directory!
		File flansDir = new File(this.pack.getInstalledDirectory(), "Flan");

		if (flansDir.exists()) {
			deleteMods(flansDir);
		}

		File packOutput = this.pack.getInstalledDirectory();

		IZipFileFilter zipFilter = new ModpackZipFilter(this.pack);

		for (Mod mod : modpack.getMods()) {
			String url = mod.getUrl();
			String md5 = mod.getMd5();

			String version = mod.getVersion();

			String name;
			if (version != null) {
				name = mod.getName() + "-" + version + ".zip";
			} else {
				name = mod.getName() + ".zip";
			}

			File cache = new File(this.pack.getCacheDir(), name);

			IFileVerifier verifier = null;

			if (md5 != null && !md5.isEmpty())
				verifier = new MD5FileVerifier(md5);
			else
				verifier = new ValidZipFileVerifier();

			//noinspection unchecked
			checkModQueue.addTask(new EnsureFileTask(cache, verifier, packOutput, url, downloadModQueue, copyModQueue, zipFilter));
		}

		copyModQueue.addTask(new CleanupModpackCacheTask(this.pack, modpack));
	}

	private void deleteMods(File modsDir) throws CacheDeleteException {
		for (File mod : modsDir.listFiles()) {
			if (mod.isDirectory()) {
				deleteMods(mod);
				continue;
			}

			if (mod.getName().endsWith(".zip") || mod.getName().endsWith(".jar") || mod.getName().endsWith(".litemod")) {
				if (!mod.delete()) {
					throw new CacheDeleteException(mod.getAbsolutePath());
				}
			}
		}
	}
}
