

package net.tharow.tantalum.platform.io;

import net.tharow.tantalum.rest.RestObject;

@SuppressWarnings({"unused"})
public class SearchResultsData extends RestObject {
    private SearchResult[] modpacks;

    public SearchResult[] getResults() {
        return modpacks;
    }
}
