

package net.tharow.tantalum.platform.io;

@SuppressWarnings({"unused"})
public class SearchResult {
    private int id;
    private String name;
    private String slug;
    private String url;

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getUrl() {
        return url;
    }
}
