

package net.tharow.tantalum.solder.io;

import net.tharow.tantalum.rest.RestObject;

import java.util.Map;

@SuppressWarnings({"unused"})
public class Solder extends RestObject {
    private transient String url;
    private Map<String, String> modpacks;
    private String mirror_url;

    public Solder() {

    }

    public Solder(String url) {
        this.url = url;
    }

    public Solder(String url, String mirrorUrl) {
        this.url = url;
        this.mirror_url = mirrorUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getModpacks() {
        return modpacks;
    }

    public String getMirrorUrl() {
        return mirror_url;
    }
}
