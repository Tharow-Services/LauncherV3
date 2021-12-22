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

import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.github.io.RepoReleasesData;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.platform.io.INewsData;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformInfo;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;

import javax.annotation.Nullable;

public class HttpPlatformApi implements IPlatformApi {
    private String platformUrl;
    private String buildnumber = TantalumConstants.getBuildNumber().getBuildNumber();
    private boolean isTechnicPlatform; //Pretend We Are a normal Technic Launcher
    private boolean isGithub;

    public HttpPlatformApi(String rootUrl) {
        this(rootUrl, false, false);
    }

    public HttpPlatformApi(){
        this(null, false, true);
    }

    public HttpPlatformApi(String platformUrl, boolean isGithub, boolean isTechnicPlatform){
        this.platformUrl = platformUrl;
        if(isTechnicPlatform){
            this.platformUrl = "https://api.technicpack.net/";
            try {
                this.buildnumber = String.valueOf(RestObject.getRestObject(StreamVersion.class, "https://api.technicpack.net/launcher/version/stable4").getBuild());
            } catch (RestfulAPIException e) {
                Utils.getLogger().warning("Couldn't Contact Technic Platform For Build Number setting build to 1024");
                this.buildnumber = String.valueOf(1024);
            }
        }
        this.isTechnicPlatform = isTechnicPlatform;
        this.isGithub = isGithub;

    }

    public String getPlatformUri(String packSlug) {
            return platformUrl + "modpack/" + packSlug + "?build="+ buildnumber;

    }

    @Override
    public IPlatformInfo getPlatformInfo() throws RestfulAPIException {
        return RestObject.getRestObject(PlatformInfo.class, platformUrl + "/ping");
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
        String url = platformUrl + "modpack/" + packSlug + "/runs?build="+buildnumber;
        Utils.pingHttpURL(url);
    }

    @Override
    public void incrementPackInstalls(String packSlug) {
        String url = platformUrl + "modpack/" + packSlug + "/install?build=" + buildnumber;
        Utils.pingHttpURL(url);
    }

    @Override
    public NewsData getNews() throws RestfulAPIException {
        return (NewsData) getNews(isTechnicPlatform, isGithub, Integer.parseInt(buildnumber), this.platformUrl);
    }

    public static NewsData getNews(String platformUrl) throws RestfulAPIException{
        INewsData result;
        if(platformUrl.contains("github")){
            result = getNews(false, true , 0,null);
        } else
        if(platformUrl.contains("technicpack")){
            result = getNews(true, false, 1024,null);
        } else {
            result = getNews(false, false, 0, platformUrl);
        }
        return (NewsData) result;

    }

    public static NewsData getNews(boolean isTechnicPlatform) throws RestfulAPIException {
        return (NewsData) getNews(isTechnicPlatform, !isTechnicPlatform, 1024, null);
    }

    protected static INewsData getNews(boolean isTechnicPlatform, boolean isGithub, int build, @Nullable String platformUrl) throws RestfulAPIException{
        if(isGithub){
            return RestObject.getRestObject(RepoReleasesData.class, "https://api.github.com/repos/Tharow-Services/Tantalum-Launcher/releases");
        }
        if(isTechnicPlatform)
            return RestObject.getRestObject(NewsData.class, "https://api.technicpack.net/news?build=" + build);
        if(platformUrl != null)
            return RestObject.getRestObject(NewsData.class, platformUrl);
        //Rerun With default
        return getNews(true, false, 1024, null);
    }
}
