

package net.tharow.tantalum.platform.io;

import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.rest.io.Resource;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class PlatformPackInfo extends RestObject implements PackInfo {
    private String name;
    private String displayName;
    private String url;
    private String platformUrl;
    private Resource icon;
    private Resource logo;
    private Resource background;
    private String minecraft;
    private String forge;
    private String version;
    private String solder;
    private String description;
    private Integer ratings;
    private Integer runs;
    private Integer downloads;
    private boolean isServer;
    private boolean isOfficial;
    private String discordServerId;

    private final ArrayList<FeedItem> feed = new ArrayList<>();

    private transient boolean isLocal = false;

    public PlatformPackInfo() {

    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getWebSite() {
        return platformUrl;
    }

    @Override
    public Resource getIcon() {
        return icon;
    }

    @Override
    public Resource getBackground() {
        return background;
    }

    @Override
    public Resource getLogo() {
        return logo;
    }

    @Override
    public String getRecommended() {
        if (hasSolder())
            return null;

        return version;
    }

    @Override
    public String getLatest() {
        if (hasSolder())
            return null;

        return version;
    }

    @Override
    public ArrayList<FeedItem> getFeed() {
        return feed;
    }


    @Override
    public List<String> getBuilds() {
        if (hasSolder()) {
            // If this is a Solder modpack, Platform modpack version has no play in the builds list
            // Code can actually reach this if the Solder instance is offline, due to how combined modpack info works
            return new ArrayList<>(0);
        }

        List<String> builds = new ArrayList<>();
        builds.add(version);
        return builds;
    }

    public String getGameVersion() {
        return minecraft;
    }

    public String getForge() {
        return forge;
    }

    public String getSolder() {
        return solder;
    }

    public boolean hasSolder() {
        return solder != null && !solder.equals("");
    }

    public String getDescription() {
        if (description == null)
            return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc facilisis congue dignissim. Aliquam posuere eros vel eros luctus molestie. Duis non massa vel orci sagittis semper. Pellentesque lorem diam, viverra in bibendum in, tincidunt in neque. Curabitur consectetur aliquam sem eget laoreet. Quisque eget turpis a velit semper dictum at ut neque. Nulla placerat odio eget neque commodo posuere. Nam porta lacus elit, a rutrum enim mollis vel.";

        return description;
    }

    public Integer getLikes() {
        return ratings;
    }

    public Integer getRuns() {
        return runs;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public boolean isServerPack() { return isServer; }

    @Override
    public Modpack getModpack(String build) {
        return new Modpack(this);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    public String getUrl() {
        return url;
    }

    public void setLocal() { isLocal = true; }
    @Override
    public boolean isLocal() {
        // If this modpack has a Solder instance set, and code has reached this point, that means that Solder is
        // unreachable for some reason, and we should consider its Solder to be offline (and mark the pack as local)
        if (hasSolder())
            return true;

        return isLocal;
    }

    @Override
    public boolean isOfficial() { return isOfficial; }

    @Override
    public String toString() {
        return "PlatformPackInfo{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", url='" + url + '\'' +
                ", icon=" + icon +
                ", logo=" + logo +
                ", background=" + background +
                ", gameVersion='" + minecraft + '\'' +
                ", forge='" + forge + '\'' +
                ", version='" + version + '\'' +
                ", solder='" + solder + '\'' +
                '}';
    }
}
