

package net.tharow.tantalum.launchercore.image;

public interface IImageJobListener<T> {
    void jobComplete(ImageJob<T> job);
}
