

package net.tharow.tantalum.platform;

import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.NewsData;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.tantalum.io.Platform;

import java.util.Collection;
import java.util.Map;

public interface IPlatformApi extends IPlatformPackApi{

    String getPlatformUri(String slug);

    void incrementPackRuns(String packSlug);

    void incrementPackInstalls(String packSlug);

    void incrementPackLikes(String packSlug);

    NewsData getNews() throws RestfulAPIException;

    Collection<HttpPlatformApi> getApis();
}
