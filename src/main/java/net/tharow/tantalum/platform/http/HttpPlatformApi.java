package net.tharow.tantalum.platform.http;

import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.tantalum.io.Platform;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class HttpPlatformApi implements IPlatformApi, IPlatformSearchApi {
    private final static Logger l = Logger.getLogger("Http Platform Api");
    private final String access;
    static {
        l.setParent(Utils.getLogger());
        l.setLevel(Utils.getLogger().getLevel());
    }
    private final IPlatformInfo platform;

    public HttpPlatformApi(@NotNull IPlatformInfo platform) {
        l.constructor("Building HttpPlatform Api From "+platform);
        this.platform = platform;
        this.access = ((platform.getAccessVerb() == null)?"":'?'+platform.getAccessVerb()+'='+platform.getAccessCode());
    }
    public HttpPlatformApi() {
        l.constructor("Building Static Platform Api");
        this.platform = new Platform("Tantalum Static","2.3","https://static.tantalum.tharow.net/platform/", false);
        this.access = "";
    }

    @Override
    public String getPlatformUri(String packSlug) {
        return platform.getUrl() + "modpack/" + packSlug + '/' + access;
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
        String url = platform.getUrl() + "modpack/" + packSlug + "/stat/run"+this.access;
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackInstalls(String packSlug) {
        String url = platform.getUrl() + "modpack/" + packSlug + "/stat/install" + this.access;
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackLikes(String packSlug) {
        String url = platform.getUrl() + "modpack/" + packSlug + "/stat/like" + this.access;
        Utils.pingHttpURL(url);
    }

    @Override
    public NewsData getNews() throws RestfulAPIException {
        String url = platform.getUrl() + "news"+'?'+this.access;
        return RestObject.getRestObject(NewsData.class, url);
    }

    @Override
    public Collection<HttpPlatformApi> getApis() {
        Collection<HttpPlatformApi> temp = new ArrayList<HttpPlatformApi>() {};
        temp.add(this);
        return temp;
    }

    @Override
    public SearchResultsData getSearchResults(String searchTerm) throws RestfulAPIException {
        String url = platform.getUrl() + "search?q=" + Utils.urlEncode(searchTerm) + this.access.replaceFirst("\\?","&");
        return RestObject.getRestObject(SearchResultsData.class, url);
    }

    @Override
    public String toString() {
        return platform.getName() + ' '+ platform.getVersion();
    }
}