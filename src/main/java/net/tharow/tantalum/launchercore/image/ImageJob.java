

package net.tharow.tantalum.launchercore.image;

import net.tharow.tantalum.utilslib.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class ImageJob<T> {
    protected final IImageMapper<T> mapper;
    protected final IImageStore<T> store;

    private T lastJobData;

    protected boolean canRetry = true;
    private final AtomicReference<BufferedImage> imageReference;

    private final Collection<IImageJobListener<T>> jobListeners = new LinkedList<>();
    private Thread imageThread;

    public ImageJob(IImageMapper<T> mapper, IImageStore<T> store) {
        this.mapper = mapper;
        this.store = store;

        imageReference = new AtomicReference<>();
        imageReference.set(mapper.getDefaultImage());
    }

    public BufferedImage getImage() {
        return imageReference.get();
    }

    public void addJobListener(IImageJobListener listener) {
        synchronized (jobListeners) {
            //noinspection unchecked
            jobListeners.add(listener);
        }
    }

    public void removeJobListener(IImageJobListener listener) {
        synchronized (jobListeners) {
            jobListeners.remove(listener);
        }
    }

    public boolean canRetry() {
        return canRetry;
    }

    public void refreshRetry() {
        canRetry = true;
    }

    public T getJobData() {
        return lastJobData;
    }

    protected void setImage(BufferedImage image) {
        canRetry = false;
        imageReference.set(image);

        notifyComplete();
    }

    protected void notifyComplete() {
        if (EventQueue.isDispatchThread()) {
            synchronized (jobListeners) {
                for (IImageJobListener<T> listener : jobListeners) {
                    listener.jobComplete(ImageJob.this);
                }
            }
        } else {
            EventQueue.invokeLater(this::notifyComplete);
        }
    }

    public void start(final T jobData) {
        lastJobData = jobData;

        if (imageThread != null && imageThread.isAlive())
            return;

        imageThread = new Thread("Image Download: " + store.getJobKey(jobData)) {
            @Override
            public void run() {
                try {
                    File imageLocation = mapper.getImageLocation(jobData);

                    BufferedImage existingImage = null;

                    if (imageLocation != null && imageLocation.exists()) {
                        try {
                            existingImage = ImageIO.read(imageLocation);
                        } catch (IOException ex) {
                            //Corrupt or missing- that's fine, just redownload it
                        }
                    }

                    if (existingImage != null)
                        setImage(existingImage);

                    if (store.canDownloadImage(jobData, imageLocation) && (existingImage == null || mapper.shouldDownloadImage(jobData))) {
                        if (imageLocation != null && !imageLocation.getParentFile().exists())
                            Utils.ignored = imageLocation.getParentFile().mkdirs();

                        store.downloadImage(jobData, imageLocation);

                        try {
                            assert imageLocation != null;
                            BufferedImage newImage = ImageIO.read(imageLocation);

                            if (newImage != null)
                                setImage(newImage);
                        } catch (IOException ex) {
                            //Again- probably something wrong with the image, so we'll just show the default
                        }
                    }
                } finally {
                    if (canRetry)
                        canRetry = store.canRetry(jobData);
                }
            }
        };
        imageThread.start();
    }
}
