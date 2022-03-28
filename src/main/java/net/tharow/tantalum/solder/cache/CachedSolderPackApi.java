

package net.tharow.tantalum.solder.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.SolderPackInfo;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CachedSolderPackApi implements ISolderPackApi {

    private final LauncherDirectories directories;
    private final ISolderPackApi innerApi;
    private final int cacheInSeconds;
    private final String packSlug;

    private SolderPackInfo rootInfoCache = null;
    private DateTime lastInfoAccess = new DateTime(0);

    private final Cache<String, Modpack> buildCache;
    private final Cache<String, Boolean> deadBuildCache;

    public CachedSolderPackApi(LauncherDirectories directories, ISolderPackApi innerApi, int cacheInSeconds, String packSlug) {
        this.directories = directories;
        this.innerApi = innerApi;
        this.cacheInSeconds = cacheInSeconds;
        this.packSlug = packSlug;

        buildCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(300)
                .expireAfterWrite(cacheInSeconds, TimeUnit.SECONDS)
                .build();

        deadBuildCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(300)
                .expireAfterWrite(cacheInSeconds / 10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getMirrorUrl() {
        return innerApi.getMirrorUrl();
    }

    @Override
    public SolderPackInfo getPackInfoForBulk() throws RestfulAPIException {
        if (rootInfoCache != null)
            return rootInfoCache;

        loadForeverCache();

        if (rootInfoCache != null)
            return rootInfoCache;

        return pullAndCache();
    }

    @Override
    public SolderPackInfo getPackInfo() throws RestfulAPIException {
        if (Seconds.secondsBetween(lastInfoAccess, DateTime.now()).isLessThan(Seconds.seconds(cacheInSeconds))) {
            if (rootInfoCache != null)
                return rootInfoCache;
        }

        if (Seconds.secondsBetween(lastInfoAccess, DateTime.now()).isLessThan(Seconds.seconds(cacheInSeconds / 10)))
            return rootInfoCache;

        try {
            return pullAndCache();
        } catch (RestfulAPIException ex) {
            ex.printStackTrace();

            return getPackInfoForBulk();
        }
    }

    private SolderPackInfo pullAndCache() throws RestfulAPIException {
        try {
            rootInfoCache = innerApi.getPackInfoForBulk();
            saveForeverCache(rootInfoCache);
            return rootInfoCache;
        } finally {
            lastInfoAccess = DateTime.now();
        }
    }

    private void loadForeverCache() {
        File cacheFile = new File(new File(new File(directories.getAssetsDirectory(), "packs"), packSlug), "soldercache.json");
        if (!cacheFile.exists())
            return;

        try {
            String packCache = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            rootInfoCache = Utils.getGson().fromJson(packCache, SolderPackInfo.class);

            if (rootInfoCache != null)
                rootInfoCache.setLocal();
        } catch (IOException | JsonSyntaxException ignored) {
        }
    }

    private void saveForeverCache(SolderPackInfo info) {
        File cacheFile = new File(new File(new File(directories.getAssetsDirectory(), "packs"), info.getName()), "soldercache.json");

        String packCache = Utils.getGson().toJson(info);

        try {
            FileUtils.writeStringToFile(cacheFile, packCache, StandardCharsets.UTF_8);
        } catch (IOException e) {
        }
    }


    @Override
    public Modpack getPackBuild(String build) throws BuildInaccessibleException {

        Boolean isDead = deadBuildCache.getIfPresent(build);

        if (isDead != null && isDead)
            return null;

        Modpack modpack = buildCache.getIfPresent(build);

        if (modpack != null) {
            return modpack;
        }

        try {
            modpack = innerApi.getPackBuild(build);

            if (modpack != null) {
                buildCache.put(build, modpack);
            }

            return modpack;
        } finally {
            deadBuildCache.put(build, modpack == null);
        }
    }
}
