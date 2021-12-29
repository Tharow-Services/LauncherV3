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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.CopyFileTask;
import net.tharow.tantalum.launchercore.install.tasks.EnsureFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.verifiers.FileSizeVerifier;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.version.MojangVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record InstallMinecraftAssetsTask(ModpackModel modpack,
                                         String assetsDirectory, File assetsIndex,
                                         ITasksQueue checkAssetsQueue,
                                         ITasksQueue downloadAssetsQueue,
                                         ITasksQueue copyAssetsQueue) implements IInstallTask {
    private final static String virtualField = "virtual";
    private final static String mapToResourcesField = "map_to_resources";
    private final static String objectsField = "objects";
    private final static String sizeField = "size";
    private final static String hashField = "hash";

    @Override
    public String getTaskDescription() {
        return "Checking Minecraft Assets";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        String json = FileUtils.readFileToString(assetsIndex, StandardCharsets.UTF_8);
        JsonObject obj = MojangUtils.getGson().fromJson(json, JsonObject.class);

        if (obj == null) {
            throw new DownloadException("The assets json file was invalid.");
        }

        boolean isVirtual = false;

        if (obj.get(virtualField) != null)
            isVirtual = obj.get(virtualField).getAsBoolean();

        boolean mapToResources = false;

        if (obj.get(mapToResourcesField) != null)
            mapToResources = obj.get(mapToResourcesField).getAsBoolean();

        //noinspection unchecked
        ((InstallTasksQueue<MojangVersion>) queue).getMetadata().setAreAssetsVirtual(isVirtual);
        //noinspection unchecked
        ((InstallTasksQueue<MojangVersion>) queue).getMetadata().setAssetsMapToResources(mapToResources);

        JsonObject allObjects = obj.get(objectsField).getAsJsonObject();

        if (allObjects == null) {
            throw new DownloadException("The assets json file was invalid.");
        }

        @SuppressWarnings("unchecked") String assetsKey = ((InstallTasksQueue<MojangVersion>) queue).getMetadata().getAssetsKey();
        if (assetsKey == null || assetsKey.isEmpty())
            assetsKey = "legacy";

        for (Map.Entry<String, JsonElement> field : allObjects.entrySet()) {
            String friendlyName = field.getKey();
            JsonObject file = field.getValue().getAsJsonObject();
            String hash = file.get(hashField).getAsString();
            long size = file.get(sizeField).getAsLong();

            File location = new File(assetsDirectory + "/objects/" + hash.substring(0, 2), hash);
            String url = MojangUtils.getResourceUrl(hash);

            (new File(location.getParent())).mkdirs();

            File target = null;

            if (isVirtual)
                target = new File(assetsDirectory + "/virtual/" + assetsKey + '/' + friendlyName);
            else if (mapToResources)
                target = new File(modpack.getResourcesDir(), friendlyName);

            //noinspection unchecked
            checkAssetsQueue.addTask(new EnsureFileTask(location, new FileSizeVerifier(size), null, url, hash, downloadAssetsQueue, copyAssetsQueue));

            if (target != null && !target.exists()) {
                (new File(target.getParent())).mkdirs();
                copyAssetsQueue.addTask(new CopyFileTask(location, target));
            }
        }
    }
}
