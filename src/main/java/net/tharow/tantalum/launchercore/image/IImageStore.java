

package net.tharow.tantalum.launchercore.image;

import java.io.File;

public interface IImageStore<T> {
    boolean canDownloadImage(T key, File target);

    void downloadImage(T key, File target);

    String getJobKey(T key);

    boolean canRetry(T key);
}
