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

import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpPlatformSearchApi implements IPlatformSearchApi {
    private String rootUrl;
    private String launcherBuild;

    public HttpPlatformSearchApi(String rootUrl, String launcherBuild) {
        this.rootUrl = rootUrl;
        this.launcherBuild = launcherBuild;
    }

    @Override
    public SearchResultsData getSearchResults(String searchTerm) throws RestfulAPIException {
        try {
            String url = rootUrl + "search.php?q=" + URLEncoder.encode(searchTerm.trim(), "UTF-8");
            return RestObject.getRestObject(SearchResultsData.class, url);
        } catch (UnsupportedEncodingException ex) {
            return new SearchResultsData();
        }
    }
}
