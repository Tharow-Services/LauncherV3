

package net.tharow.tantalum.solder.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderInfo;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.SolderPackInfo;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class CachedSolderApi implements ISolderApi {

    private final LauncherDirectories directories;
    private final ISolderApi innerApi;
    private Collection<SolderPackInfo> cachedPublicPacks = null;
    private DateTime lastSolderPull = new DateTime(0);
    private final int cacheInSeconds;

    private static class CacheTuple {
        private final String root;
        private final String slug;
        private final String url;

        public CacheTuple(String root, String slug, String url) {
            this.root = root;
            this.slug = slug;
            this.url = url;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof CacheTuple))
                return false;

            if (!root.equals(((CacheTuple) obj).root))
                return false;
            if (!slug.equals(((CacheTuple) obj).slug))
                return false;
            return url.equals(((CacheTuple) obj).url);
        }

        @Override
        public int hashCode() {
            int hash = root.hashCode();
            hash *= 31;
            hash += slug.hashCode();
            hash *= 31;
            hash += url.hashCode();
            return hash;
        }
    }

    private final Cache<CacheTuple, ISolderPackApi> packs;

    public CachedSolderApi(LauncherDirectories directories, ISolderApi innerApi, int cacheInSeconds) {
        this.directories = directories;
        this.innerApi = innerApi;
        this.cacheInSeconds = cacheInSeconds;

        packs = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(50)
                .expireAfterWrite(cacheInSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public ISolderInfo getSolderInfo(String solderRoot) throws RestfulAPIException {
        return innerApi.getSolderInfo(solderRoot);
    }

    @Override
    public ISolderPackApi getSolderPack(String solderRoot, String modpackSlug, String mirrorUrl) throws RestfulAPIException {
        CacheTuple tuple = new CacheTuple(solderRoot, modpackSlug, mirrorUrl);
        ISolderPackApi pack = packs.getIfPresent(tuple);

        if (pack == null) {
            pack = new CachedSolderPackApi(directories, innerApi.getSolderPack(solderRoot, modpackSlug, mirrorUrl), cacheInSeconds, modpackSlug);
            packs.put(tuple, pack);
        }

        return pack;
    }

    @Override
    public Collection<SolderPackInfo> getPublicSolderPacks(String solderRoot) throws RestfulAPIException {
        return internalGetPublicSolderPacks(solderRoot, this);
    }

    @Override
    public Collection<SolderPackInfo> internalGetPublicSolderPacks(String solderRoot, ISolderApi packFactory) throws RestfulAPIException {
        if (Seconds.secondsBetween(lastSolderPull, DateTime.now()).isLessThan(Seconds.seconds(cacheInSeconds))) {
            if (cachedPublicPacks != null)
                return cachedPublicPacks;
        }

        if (Seconds.secondsBetween(lastSolderPull, DateTime.now()).isLessThan(Seconds.seconds(cacheInSeconds / 10)))
            return new ArrayList<>(0);

        try {
            cachedPublicPacks = innerApi.internalGetPublicSolderPacks(solderRoot, this);
            return cachedPublicPacks;
        } finally {
            lastSolderPull = DateTime.now();
        }
    }

    @Override
    public String getMirrorUrl(String solderRoot) throws RestfulAPIException {
        return innerApi.getMirrorUrl(solderRoot);
    }
}
