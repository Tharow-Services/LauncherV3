

package net.tharow.tantalum.launchercore.image;

import java.awt.image.BufferedImage;
import java.io.File;

public interface IImageMapper<T> {
    boolean shouldDownloadImage(T imageKey);

    File getImageLocation(T imageKey);

    BufferedImage getDefaultImage();
}
