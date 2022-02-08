package net.tharow.tantalum.platform.http;

import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;

public class HttpPlatformApi implements IPlatformApi {
    private final static Logger l = Logger.getLogger("HttpPlatformApi");
    static {
        l.setParent(Utils.getLogger());
        l.setLevel(Utils.getLogger().getLevel());
    }
    private final IPlatformInfo platform;

    public HttpPlatformApi(IPlatformInfo platform) {
        this.platform = platform;
    }

    public String getPlatformUri(String packSlug) {
        return platform.get() + "modpack/" + packSlug + "?build="+platform.getBuild();
    }

    @Override
    public PlatformPackInfo getPlatformPackInfoForBulk(String packSlug) throws RestfulAPIException {
        return getPlatformPackInfo(packSlug);
    }

    @Override
    public PlatformPackInfo getPlatformPackInfo(String packSlug) throws RestfulAPIException {
        String url = getPlatformUri(packSlug);
        return RestObject.getRestObject(PlatformPackInfo.class, url);
    }

    @Override
    public void incrementPackRuns(String packSlug) {
        String url = platform.get() + "modpack/" + packSlug + "/stat/run?build="+platform.getBuild();
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackInstalls(String packSlug) {
        String url = platform.get() + "modpack/" + packSlug + "/stat/install?build="+platform.getBuild();
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackLikes(String packSlug) {
        String url = platform.get() + "modpack/" + packSlug + "/stat/like?build="+platform.getBuild();
    }

    @Override
    public NewsData getNews() throws RestfulAPIException {
        String url = platform.get() + "news?build="+platform.getBuild();
        return RestObject.getRestObject(NewsData.class, url);
    }
}