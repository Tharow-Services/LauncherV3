

package net.tharow.tantalum.platform;

import net.tharow.tantalum.platform.io.SearchResultsData;
import net.tharow.tantalum.rest.RestfulAPIException;

public interface IPlatformSearchApi {
    SearchResultsData getSearchResults(String searchTerm) throws RestfulAPIException;
}
