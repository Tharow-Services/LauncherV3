

package net.tharow.tantalum.launchercore.image;

import java.util.HashMap;
import java.util.Map;

public class ImageRepository<T> {
    private final IImageMapper<T> mapper;
    private final IImageStore<T> store;
    private final Map<String, ImageJob> allJobs = new HashMap<>();

    public ImageRepository(IImageMapper<T> mapper, IImageStore<T> store) {
        this.mapper = mapper;
        this.store = store;
    }

    public ImageJob<T> startImageJob(T key) {
        String jobKey = store.getJobKey(key);

        ImageJob<T> job = null;
        if (allJobs.containsKey(jobKey))
            //noinspection unchecked
            job = allJobs.get(jobKey);
        else {
            job = new ImageJob<>(mapper, store);
            allJobs.put(jobKey, job);
        }

        if (job.canRetry())
            job.start(key);

        return job;
    }

    public void refreshRetry(T key) {
        String jobKey = store.getJobKey(key);

        if (allJobs.containsKey(jobKey))
            allJobs.get(jobKey).refreshRetry();
    }
}
