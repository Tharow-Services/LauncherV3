

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

    private final LauncherDirectories directories;
    private final BufferedImage defaultImage;
    private final IModpackResourceType resourceType;

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
