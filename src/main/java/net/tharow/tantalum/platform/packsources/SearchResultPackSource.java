

package net.tharow.tantalum.platform.packsources;

import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.SearchResult;
import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.utilslib.Utils;

import java.util.*;

public class SearchResultPackSource implements IPackSource {
    private final IPlatformSearchApi platform;
    private final String searchTerms;
    private final Map<String, Integer> resultPriorities = new HashMap<>();

    public SearchResultPackSource(IPlatformSearchApi searchApi, String searchTerms) {
        this.platform = searchApi;
        this.searchTerms = searchTerms;
        Utils.getLogger().constructor("Built "+getSourceName());
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
            results = platform.getSearchResults(searchTerms);
        } catch (RestfulAPIException ex) {
            ex.printStackTrace();
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
