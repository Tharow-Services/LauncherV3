

package net.tharow.tantalum.launcher.io;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launchercore.image.IImageMapper;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.platform.io.AuthorshipInfo;

import java.awt.image.BufferedImage;
import java.io.File;

public class TantalumAvatarMapper implements IImageMapper<AuthorshipInfo> {
    private final LauncherDirectories directories;
    private final BufferedImage defaultImage;

    public TantalumAvatarMapper(LauncherDirectories directories, ResourceLoader resources) {
        this.directories = directories;
        defaultImage = resources.getImage("icon.png");
    }

    @Override
    public boolean shouldDownloadImage(AuthorshipInfo imageKey) {
        return true;
    }

    @Override
    public File getImageLocation(AuthorshipInfo imageKey) {
        return new File(directories.getAssetsDirectory(), "avatars" + File.separator + "gravitar" + File.separator + imageKey.getUser() + ".png");
    }

    @Override
    public BufferedImage getDefaultImage() {
        return defaultImage;
    }
}
