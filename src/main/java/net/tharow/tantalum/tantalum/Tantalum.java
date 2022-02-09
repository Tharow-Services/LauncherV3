package net.tharow.tantalum.tantalum;

import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.packinfo.CombinedPackInfo;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformPackApi;
import net.tharow.tantalum.platform.cache.ModpackCachePlatformApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.SolderPackInfo;
import net.tharow.tantalum.tantalum.io.Platform;
import net.tharow.tantalum.tantalum.store.PlatformStore;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class Tantalum implements IAuthoritativePackSource, IPlatformApi {

    public static Logger logger = Logger.getLogger("Tantalum Platform Source");
    static {
        logger.setParent(Utils.getLogger());
        logger.setLevel(Utils.getLogger().getLevel());
    }
    private final PlatformStore store;
    private final ISolderApi solderCache;
    private final ModpackCachePlatformApi platformCache;
    private final Map<UUID, HttpPlatformApi> platformApi = new HashMap<>();

    private Tantalum(PlatformStore store, ISolderApi solderApi, ModpackCachePlatformApi platformCache){
        this.store = store;
        this.solderCache = solderApi;
        this.platformCache = platformCache;
        init();
    }
    protected void init(){
        store.refresh();
        synchronized (platformApi) {
            store.getPlatforms().values().stream().filter(platform -> !platform.isOffline()).forEach((platform)->platformApi.put(platform.get(), new HttpPlatformApi(platform)));
        }
    }

    public static @NotNull Tantalum init(LauncherDirectories directories, ISolderApi solderApi, int cacheInSeconds){
        final ModpackCachePlatformApi platformCache = new ModpackCachePlatformApi(cacheInSeconds, directories);
        final PlatformStore store = PlatformStore.load(new File(directories.getLauncherDirectory(), "platforms.json"));
        return new Tantalum(store, solderApi, platformCache);
    }

    @Override
    public PackInfo getPackInfo(@NotNull InstalledPack pack) {
        return getPackInfo(pack.getName());
    }

    @Override
    public PackInfo getCompletePackInfo(@NotNull PackInfo pack) {
        return getPackInfo(pack.getName());
    }

    @Override
    public PlatformPackInfo getPlatformPackInfoForBulk(String packSlug) throws RestfulAPIException {
        return getPlatformPackInfo(packSlug);
    }

    @Override
    public PlatformPackInfo getPlatformPackInfo(String slug) throws RestfulAPIException {
        logger.config("System Status: Active");
        logger.fine("Trying to find slug of "+slug+" In Slug Dictionary");
        if (store.getBySlug().contains(slug)) {
            logger.config("A modpack with slug: "+slug+" was found in the dictionary");
            return null;
        }
        logger.fine("A Modpack with slug: "+slug+" wasn't found in the dictionary");
        logger.fine("Grabbing Active Server List And Running Slugs");
        PlatformPackInfo returned = null;
        for (Map.Entry<UUID, HttpPlatformApi> entry : platformApi.entrySet()) {
            //UUID ignored = entry.getKey();
            HttpPlatformApi platform = entry.getValue();
            PlatformPackInfo current = null;
            try {
                current = platform.getPlatformPackInfo(slug);
            } catch (RestfulAPIException e) {
                e.printStackTrace();
                logger.finer("Pack Wasn't Found At: " + platform);
            }
            if (current != null) {
                logger.finer("Pack "+slug+" was Found at: "+platform);
                store.put(slug, entry.getKey());
                returned = current;
                break;
            }
        }
        if (returned != null){
            return returned;
        }
        logger.severe("Was Unable to find Source for Pack with slug "+slug);
        throw new RestfulAPIException("Was Unable to find Source for Pack with slug "+slug);
    }

    public void addPlatform(String url, String accessVerb, String accessCode) throws RestfulAPIException, RequiresAccessCode {
        final Platform platform = new Platform(url, accessVerb, accessCode);
        this.platformApi.put(platform.get(), new HttpPlatformApi(platform));
        this.store.put(platform);
    }

    public Collection<Platform> getPlatforms() {
        return store.getPlatforms().values();
    }

    public HttpPlatformApi getPlatformApi(UUID uuid){
        return platformApi.get(uuid);
    }

    protected HttpPlatformApi getPlatformApi(String slug){
        return platformApi.get(store.getDictionary().get(slug));
    }

    protected PackInfo getPackInfo(String slug) {
        try {
            PackInfo info = null;
            PlatformPackInfo platformInfo;
            try {
                platformInfo = platformCache.getPlatformPackInfoForBulk(slug);
            } catch (RestfulAPIException ignored){
                platformInfo = getPlatformPackInfoForBulk(slug);
            }

            info = getInfoFromPlatformInfo(platformInfo);

            return info;
        } catch (RestfulAPIException ex) {
            logger.log(Level.WARNING, "Unable to load platform pack " + slug, ex);
            return null;
        }
    }

    protected PackInfo getInfoFromPlatformInfo(PlatformPackInfo platformInfo) {
        if (platformInfo != null && platformInfo.hasSolder()) {
            try {
                ISolderPackApi solderPack = solderCache.getSolderPack(platformInfo.getSolder(), platformInfo.getName(), solderCache.getMirrorUrl(platformInfo.getSolder()));
                SolderPackInfo solderInfo = solderPack.getPackInfoForBulk();

                if (solderInfo == null)
                    return platformInfo;
                else
                    return new CombinedPackInfo(solderInfo, platformInfo);
            } catch (RestfulAPIException ex) {
                ex.printStackTrace();
                return platformInfo;
            }
        } else {
            return platformInfo;
        }
    }

    @Override
    public String getPlatformUri(String slug) {
        return getPlatformApi(slug).getPlatformUri(slug);
    }

    @Override
    public void incrementPackRuns(String packSlug) {
        getPlatformApi(packSlug).incrementPackRuns(packSlug);
    }

    @Override
    public void incrementPackInstalls(String packSlug) {
        getPlatformApi(packSlug).incrementPackInstalls(packSlug);
    }

    @Override
    public void incrementPackLikes(String packSlug) {
        getPlatformApi(packSlug).incrementPackLikes(packSlug);
    }

    @Override
    @Deprecated
    public NewsData getNews() throws RestfulAPIException {
        return null;
    }
}
