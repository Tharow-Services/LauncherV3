package net.tharow.tantalum.minecraftcore.mojang.version.io;

@SuppressWarnings({"unused"})
public class Artifact {
    private String url;
    private String sha1;
    private Integer size;

    public Artifact(String url, String sha1, Integer size) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public String getSha1() {
        return sha1;
    }

    public Integer getSize() {
        return size;
    }
}
