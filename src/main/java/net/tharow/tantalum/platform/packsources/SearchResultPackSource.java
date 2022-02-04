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

package net.tharow.tantalum.platform.packsources;

import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.SearchResult;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;

import java.util.*;

public class SearchResultPackSource implements IPackSource {
    private final String platform;
    private final String searchTerms;
    private String buildNumber;
    private final Map<String, Integer> resultPriorities = new HashMap<>();

    public SearchResultPackSource(String platform, String searchTerms) {
        this.platform = platform;
        this.searchTerms = searchTerms;
    }

    @Override
    public String getSourceName() {
        return "Modpack search results for query '" + searchTerms + " And " + platform + "'";
    }

    @Override
    //Get PlatformPackInfo objects for every result from the given search terms.
    public Collection<PackInfo> getPublicPacks() {
        resultPriorities.clear();
        //Get results from server
        SearchResultsData results = null;
        try {
            results = HttpPlatformApi.getSearchResults(searchTerms,platform,800);
        } catch (RestfulAPIException ex) {
            return Collections.emptySet();
        }

        ArrayList<PackInfo> resultPacks = new ArrayList<>(results.getResults().length);

        int priority = 100;
        for (SearchResult result : results.getResults()) {
            resultPacks.add(new SearchResultPackInfo(result));
            resultPriorities.put(result.getSlug(), priority--);
        }

        return resultPacks;
    }

    @Override
    public int getPriority(PackInfo info) {
        if (resultPriorities.containsKey(info.getName()))
            return resultPriorities.get(info.getName());
        return 0;
    }
}
