/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launchercore.modpacks.resources;

import net.tharow.tantalum.launchercore.image.IImageMapper;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.IModpackResourceType;
import net.tharow.tantalum.rest.io.Resource;
import net.tharow.tantalum.utilslib.MD5Utils;

import java.awt.image.BufferedImage;
import java.io.File;

public class PackResourceMapper implements IImageMapper<ModpackModel> {

    private LauncherDirectories directories;
    private BufferedImage defaultImage;
    private IModpackResourceType resourceType;

    public PackResourceMapper(LauncherDirectories directories, BufferedImage defaultImage, IModpackResourceType resourceType) {
        this.directories = directories;
        this.defaultImage = defaultImage;
        this.resourceType = resourceType;
    }

    @Override
    public boolean shouldDownloadImage(ModpackModel imageKey) {
        Resource res = resourceType.getResource(imageKey);

        if (res == null)
            return false;
        if (res.getMd5() == null || res.getMd5().isEmpty())
            return true;

        return !MD5Utils.checkMD5(getImageLocation(imageKey), res.getMd5());
    }

    @Override
    public File getImageLocation(ModpackModel imageKey) {
        File assets = new File(directories.getAssetsDirectory(), "packs");
        File packs = new File(assets, imageKey.getName());
        return new File(packs, resourceType.getImageName());
    }

    @Override
    public BufferedImage getDefaultImage() {
        return defaultImage;
    }
}
