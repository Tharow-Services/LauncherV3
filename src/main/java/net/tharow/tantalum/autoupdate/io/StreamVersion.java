

package net.tharow.tantalum.autoupdate.io;

import net.tharow.tantalum.rest.RestObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class StreamVersion extends RestObject {
    private int build;
    private StreamUrls url;
    private final List<LauncherResource> resources = new ArrayList<>();

    public int getBuild() {
        return build;
    }

    public String getExeUrl() {
        return url.getExeUrl();
    }

    public String getJarUrl() {
        return url.getJarUrl();
    }

    public List<LauncherResource> getResources() { return resources; }
}
