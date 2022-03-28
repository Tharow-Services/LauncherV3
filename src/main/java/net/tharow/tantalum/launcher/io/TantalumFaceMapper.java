

package net.tharow.tantalum.launcher.io;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.image.IImageMapper;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;

import java.awt.image.BufferedImage;
import java.io.File;

public class TantalumFaceMapper implements IImageMapper<IUserType> {
    private final LauncherDirectories directories;
    private final BufferedImage defaultImage;

    public TantalumFaceMapper(LauncherDirectories directories, ResourceLoader resources) {
        this.directories = directories;
        defaultImage = resources.getImage("news/authorHelm.png");
    }

    @Override
    public boolean shouldDownloadImage(IUserType imageKey) {
        return true;
    }

    @Override
    public File getImageLocation(IUserType imageKey) {
        return new File(directories.getAssetsDirectory(), "avatars" + File.separator + imageKey.getDisplayName() + ".png");
    }

    @Override
    public BufferedImage getDefaultImage() {
        return defaultImage;
    }
}
