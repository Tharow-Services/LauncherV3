package net.tharow.tantalum.platform.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformPackApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.INewsData;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class ModpackCachePlatformApi implements IPlatformPackApi {

    private final Cache<String, PlatformPackInfo> cache;
    private final Cache<String, Boolean> deadPacks;
    private final Cache<String, PlatformPackInfo> foreverCache;
    private final LauncherDirectories directories;

    public ModpackCachePlatformApi(int cacheInSeconds, LauncherDirectories directories) {
        this.directories = directories;
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(300)
                .expireAfterWrite(cacheInSeconds, TimeUnit.SECONDS)
                .build();

        foreverCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(300)
                .build();

        deadPacks = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(300)
                .expireAfterWrite(cacheInSeconds / 10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public PlatformPackInfo getPlatformPackInfoForBulk(String packSlug) throws RestfulAPIException {

        PlatformPackInfo info = foreverCache.getIfPresent(packSlug);

        if (info == null) {
            info = loadForeverCache(packSlug);
        }

        if (info == null && isDead(packSlug))
            return null;

        if (info == null) {
            throw new RestfulAPIException("Not In Cache");
        }

        return info;
    }

    @Override
    public PlatformPackInfo getPlatformPackInfo(String packSlug) {
        PlatformPackInfo info = cache.getIfPresent(packSlug);

        if (info == null && isDead(packSlug))
            return getDeadPackInfo(packSlug);

        return info;
    }

    public void setDeadPack(String slug){
        deadPacks.put(slug, true);
    }

    protected PlatformPackInfo getDeadPackInfo(String packSlug) {
        try {
            PlatformPackInfo deadInfo = getPlatformPackInfoForBulk(packSlug);

            if (deadInfo != null)
                deadInfo.setLocal();
            return deadInfo;
        } catch (RestfulAPIException ex) {
            return null;
        }
    }

    private boolean isDead(String packSlug) {
        Boolean isDead = deadPacks.getIfPresent(packSlug);

        return isDead != null && isDead;
    }

    public void put(@NotNull PlatformPackInfo packInfo){
        cache.put(packInfo.getName(), packInfo);
        foreverCache.put(packInfo.getName(), packInfo);
        saveForeverCache(packInfo);
        deadPacks.put(packInfo.getName(), false);
    }

    private @Nullable PlatformPackInfo loadForeverCache(String packSlug) {
        File cacheFile = new File(new File(new File(directories.getAssetsDirectory(), "packs"), packSlug), "cache.json");
        if (!cacheFile.exists())
            return null;

        try {
            String packCache = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            PlatformPackInfo info = Utils.getGson().fromJson(packCache, PlatformPackInfo.class);

            if (info != null) {
                foreverCache.put(packSlug, info);
            }

            return info;
        } catch (IOException | JsonSyntaxException ex) {
            return null;
        }
    }

    private void saveForeverCache(@NotNull PlatformPackInfo info) {
        File cacheFile = new File(new File(new File(directories.getAssetsDirectory(), "packs"), info.getName()), "cache.json");

        String packCache = Utils.getGson().toJson(info);
        try {
            FileUtils.writeStringToFile(cacheFile, packCache, StandardCharsets.UTF_8);
        } catch (IOException ignored) {}
    }
}
