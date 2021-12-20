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
import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class HttpPlatformSearchApi implements IPlatformSearchApi {
    private String rootUrl;
    private boolean isTechnicPlatform;
    private String buildnumber;

    public HttpPlatformSearchApi(String rootUrl) {
        this.rootUrl = rootUrl;
        this.isTechnicPlatform = rootUrl.toLowerCase(Locale.ROOT).contains("technicpack.net");
        if(this.isTechnicPlatform){
            try {
                this.buildnumber = String.valueOf(RestObject.getRestObject(StreamVersion.class, "https://api.technicpack.net/launcher/version/stable4").getBuild());
            } catch (RestfulAPIException e) {
                Utils.getLogger().warning("Couldn't Contact Technic Platform For Build Number");
            }
        }
    }

    public HttpPlatformSearchApi() {
        this.rootUrl ="https://api.technicpack.net/";
        this.isTechnicPlatform = true;
        try {
            this.buildnumber = String.valueOf(RestObject.getRestObject(StreamVersion.class, "https://api.technicpack.net/launcher/version/stable4").getBuild());
        } catch (RestfulAPIException e) {
            Utils.getLogger().warning("Couldn't Contact Technic Platform For Build Number");
        }
    }

    @Override
    public SearchResultsData getSearchResults(String searchTerm) throws RestfulAPIException {
        try {
            String url = rootUrl + "search?q=" + URLEncoder.encode(searchTerm.trim(), "UTF-8") + "&build=" + this.buildnumber;
            return RestObject.getRestObject(SearchResultsData.class, url);
        } catch (UnsupportedEncodingException ex) {
            return new SearchResultsData();
        }
    }
}
