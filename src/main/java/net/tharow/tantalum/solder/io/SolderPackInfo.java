

package net.tharow.tantalum.solder.io;

import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.platform.io.FeedItem;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.rest.io.Resource;
import net.tharow.tantalum.solder.ISolderPackApi;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class SolderPackInfo extends RestObject implements PackInfo {

    private String name;
    private String display_name;
    private String url;
    private String icon;
    private String icon_md5;
    private String logo;
    private String logo_md5;
    private String background;
    private String background_md5;
    private String recommended;
    private String latest;
    private List<String> builds;
    private transient ISolderPackApi solder;
    private transient boolean isLocal = false;

    public SolderPackInfo() {

    }

    public ISolderPackApi getSolder() {
        return solder;
    }

    public void setSolder(ISolderPackApi solder) {
        this.solder = solder;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return display_name;
    }

    @Override
    public String getWebSite() {
        return null;
    }

    @Override
    public Resource getIcon() {
        return null;
    }

    @Override
    public Resource getBackground() {
        return null;
    }

    @Override
    public Resource getLogo() {
        return null;
    }

    @Override
    public String getRecommended() {
        return recommended;
    }

    @Override
    public String getLatest() {
        return latest;
    }

    @Override
    public List<String> getBuilds() {
        return builds;
    }


    @Override
    public ArrayList<FeedItem> getFeed() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Integer getLikes() {
        return null;
    }

    @Override
    public Integer getDownloads() {
        return null;
    }

    @Override
    public Integer getRuns() {
        return null;
    }

    @Override
    public boolean isServerPack() { return false; }

    @Override
    public boolean isOfficial() { return false; }

    @Override
    public Modpack getModpack(String build) throws BuildInaccessibleException {
        return solder.getPackBuild(build);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isLocal() {
        if (builds.size() == 0)
            return true;

        return isLocal;
    }

    public void setLocal() { isLocal = true; }

    @Override
    public boolean hasSolder() {
        return true;
    }

    @Override
    public String toString() {
        return "SolderPackInfo{" +
                "name='" + name + '\'' +
                ", display_name='" + display_name + '\'' +
                ", url='" + url + '\'' +
                ", icon_md5='" + icon_md5 + '\'' +
                ", logo_md5='" + logo_md5 + '\'' +
                ", background_md5='" + background_md5 + '\'' +
                ", recommended='" + recommended + '\'' +
                ", latest='" + latest + '\'' +
                ", builds=" + builds +
                ", solder=" + solder +
                '}';
    }
}
