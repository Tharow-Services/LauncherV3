/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.platform.http;

import net.tharow.tantalum.github.io.RepoReleasesData;
import net.tharow.tantalum.launcher.io.Platform;
import net.tharow.tantalum.launcher.io.TantalumPlatformStore;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.io.INewsData;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static net.tharow.tantalum.utilslib.Utils.*;

public class HttpPlatformApi implements IPlatformApi, IPlatformSearchApi {
    private static final Logger l = Utils.getLogger();
    private static final String buildnumber = TantalumConstants.getBuildNumber().getBuildNumber();
    private final TantalumPlatformStore store;


    public HttpPlatformApi(TantalumPlatformStore store){
        this.store = store;
        Utils.getLogger().config("Http Platform Has Been Constructed");
    }

    @Deprecated
    public String getPlatformUri(String packSlug) {
        return null;
    }

    public String getPlatformUri(String platform, String packSlug){
        return getPlatformUri(platform,packSlug, Integer.parseInt(buildnumber));
    }

    @Contract(pure = true)
    private @NotNull String getPlatformUri(String platform, String packSlug, int buildnumber){
        return platform + "modpack/" + packSlug + "/?build="+ buildnumber;
    }

    @Override
    public PlatformPackInfo getPlatformPackInfoForBulk(String packSlug) throws RestfulAPIException {
        return getPlatformPackInfo(packSlug);
    }

    @Override
    public PlatformPackInfo getPlatformPackInfo(String packSlug) throws RestfulAPIException {
        entering(this.getClass(), "getPlatformPackInfo(String packSlug)",packSlug);
        //Utils.getLogger().warning("Using old get platform uri method please don't use this");
        logDebug("Trying to find modpack with slug of: "+packSlug);
        //Check Slug dict for the slug, so we can skip this//
        if (this.store.getSlugHost(packSlug) != null) {
            logDebug("The Slug of: "+packSlug+" was found in the slug dict");
            PlatformPackInfo temp = RestObject.getRestObject(PlatformPackInfo.class,getPlatformUri(store.getSlugUrl(packSlug),packSlug));
            exiting(this.getClass(), "getPlatformPackInfo(String packSlug)",temp);
            return temp;
        }
        logDebug("The pack wasn't found in dict trying our known platforms");
        //Time to Check our Platforms to see if any of them have this pack//
        for (String hosts : this.store.getHosts()) {
            Platform hostPlatform = store.getHostPlatform(hosts);// the last time we should need to access the store
            PlatformPackInfo packInfo = getPlatformSlugPack(hostPlatform, packSlug);
            if (packInfo != null) {
                store.putSlug(packSlug, hosts); // if it was found add it to the dict
                logDebug("Found the modpack adding to dict and Returning");
                exiting(this.getClass(), "getPlatformPackInfo(String packSlug)", packInfo);
                return packInfo;
            }
        }
        // Utils.logDebug("Unable to find modpack");
        exiting(this.getClass(), "getPlatformPackInfo(String packSlug)", new RestfulAPIException("Unable to find Modpack"));
        throw new RestfulAPIException("Unable To Find Modpack");
    }
    protected PlatformPackInfo getPlatformSlugPack(@NotNull Platform platform,@NotNull String packSlug){
        entering(this.getClass(),"getPlatformSlugPack(Platform,String)", new Object[]{platform,packSlug});
        final String name = platform.getName();
        final String platformUrl = getPlatformUri(platform.getUrl(), packSlug, platform.getBuild());
        Utils.getLogger().info("Trying to get modpack with Slug: " + packSlug + " From: " + name);
        logDebug("With Url: "+platformUrl);
        PlatformPackInfo newPack = null;
        try {
            newPack = RestObject.getRestObject(PlatformPackInfo.class, platformUrl);
        } catch (RestfulAPIException e) {
            Utils.getLogger().warning("Platform: "+ name +" Gave us an Error: " + e.getMessage());
            //e.printStackTrace();
        }
        if (newPack != null) {
            if (!newPack.hasError()) {
                Utils.getLogger().info("Got Modpack: " + newPack.getDisplayName() + " From Platform: "+name);
                logDebug("With Url of "+platformUrl);
            } else {
                Utils.getLogger().severe("The modpack from: "+name+" has an error: "+newPack.getError()+" Discarding Modpack");
            }
        } else {
            logDebug("The Modpack from "+name+" was null ");
        }
        exiting(this.getClass(),"getPlatformSlugPack(Platform,String)", newPack);
        return newPack;
    }

    @Override
    public void incrementPackRuns(String packSlug) {
        entering(this.getClass(), "incrementPackRuns(String packSlug)",packSlug);
        Utils.logDebug("Incrementing Pack Runs for slug: "+packSlug);
        final String platformUrl = store.getSlugUrl(packSlug);
        if (platformUrl == null) {
            l.severe("While Trying to increment pack runs, we were unable to find the owner of the modpack slug: "+packSlug);
            return;
        }
        String url = platformUrl + "modpack/" + packSlug + "/run?build=" + buildnumber;
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackInstalls(String packSlug) {
        Utils.logDebug("Incrementing Pack Installs for Slug: "+packSlug);
        final String platformUrl = store.getSlugUrl(packSlug);
        if (platformUrl == null) {
            l.severe("While Trying to increment pack installs, we were unable to find the owner of the modpack slug: "+packSlug);
            return;
        }
        String url = platformUrl + "modpack/" + packSlug + "/install?build=" + buildnumber;
        Utils.pingHttpURL(url);
    }


    public static @NotNull NewsData getTechnicNews() throws RestfulAPIException{
        return RestObject.getRestObject(NewsData.class, "https://api.technicpack.net/news?build=" + buildnumber);
    }

    public static @NotNull INewsData getNews() throws RestfulAPIException{
        return RepoReleasesData.getRestObject("https://api.github.com/repos/Tharow-Services/Tantalum-Launcher/releases");
    }

    @Override
    public SearchResultsData getSearchResults(String searchTerm) throws RestfulAPIException {
        return getSearchResults(searchTerm, "https://tantalum-auth.azurewebsites.net/platform/", Integer.parseInt(buildnumber));
    }

    public static @NotNull SearchResultsData getFeaturedPacks(String platform) throws RestfulAPIException {
        String url = platform + "search?featured&build=" + buildnumber;
        return RestObject.getRestObject(SearchResultsData.class, url);
    }

    public static @NotNull SearchResultsData getSearchResults(@NotNull String searchTerm, String platform, int buildnumber) throws RestfulAPIException
    {
        String url = platform + "search?q=" + Utils.urlEncoder(searchTerm.trim()) + "&build=" + buildnumber;
        return RestObject.getRestObject(SearchResultsData.class, url);
    }
}
