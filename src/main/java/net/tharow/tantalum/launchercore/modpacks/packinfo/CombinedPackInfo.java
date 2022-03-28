

package net.tharow.tantalum.launchercore.modpacks.packinfo;

import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.platform.io.FeedItem;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.rest.io.Resource;

import java.util.ArrayList;
import java.util.List;

public class CombinedPackInfo implements PackInfo {
    private final PackInfo solderPackInfo;
    private final PackInfo platformPackInfo;

    public CombinedPackInfo(PackInfo solderPackInfo, PackInfo platformPackInfo) {
        this.solderPackInfo = solderPackInfo;
        this.platformPackInfo = platformPackInfo;
    }


    @Override
    public String getName() {
        if (platformPackInfo != null)
            return platformPackInfo.getName();
        if (solderPackInfo != null)
            return solderPackInfo.getName();

        return null;
    }

    @Override
    public String getDisplayName() {
        if (platformPackInfo != null)
            return platformPackInfo.getDisplayName();
        if (solderPackInfo != null)
            return solderPackInfo.getDisplayName();

        return null;
    }

    @Override
    public String getWebSite() {
        if (platformPackInfo != null)
            return platformPackInfo.getWebSite();
        if (solderPackInfo != null)
            return solderPackInfo.getWebSite();

        return null;
    }


    @Override
    public Resource getIcon() {
        if (platformPackInfo != null)
            return platformPackInfo.getIcon();

        return null;
    }

    @Override
    public Resource getBackground() {
        if (platformPackInfo != null)
            return platformPackInfo.getBackground();
        return null;
    }

    @Override
    public Resource getLogo() {
        if (platformPackInfo != null)
            return platformPackInfo.getLogo();

        return null;
    }

    @Override
    public String getRecommended() {
        if (solderPackInfo != null)
            return solderPackInfo.getRecommended();
        if (platformPackInfo != null)
            return platformPackInfo.getRecommended();

        return null;
    }

    @Override
    public String getLatest() {
        if (solderPackInfo != null)
            return solderPackInfo.getLatest();
        if (platformPackInfo != null)
            return platformPackInfo.getRecommended();

        return null;
    }

    @Override
    public List<String> getBuilds() {
        if (solderPackInfo != null)
            return solderPackInfo.getBuilds();
        if (platformPackInfo != null)
            return platformPackInfo.getBuilds();

        return new ArrayList<>(0);
    }

    @Override
    public ArrayList<FeedItem> getFeed() {
        if (platformPackInfo != null)
            return platformPackInfo.getFeed();

        return null;
    }

    @Override
    public String getDescription() {
        if (platformPackInfo != null)
            return platformPackInfo.getDescription();

        return null;
    }

    @Override
    public Integer getRuns() {
        if (platformPackInfo != null)
            return platformPackInfo.getRuns();
        return null;
    }

    @Override
    public Integer getDownloads() {
        if (platformPackInfo != null)
            return platformPackInfo.getDownloads();

        return null;
    }

    @Override
    public Integer getLikes() {
        if (platformPackInfo != null)
            return platformPackInfo.getLikes();

        return null;
    }

    @Override
    public boolean isServerPack() {
        if (platformPackInfo != null)
            return platformPackInfo.isServerPack();

        return false;
    }

    @Override
    public boolean isOfficial() {
        if (platformPackInfo != null)
            return platformPackInfo.isOfficial();

        return false;
    }

    @Override
    public Modpack getModpack(String build) throws BuildInaccessibleException {
        if (solderPackInfo != null)
            return solderPackInfo.getModpack(build);
        if (platformPackInfo != null)
            return platformPackInfo.getModpack(build);

        return null;
    }

    @Override
    public boolean isComplete() {
        return (platformPackInfo != null);
    }

    @Override
    public boolean isLocal() {
        if (solderPackInfo != null)
            return solderPackInfo.isLocal();
        if (platformPackInfo != null)
            return platformPackInfo.isLocal();

        return true;
    }

    @Override
    public boolean hasSolder() {
        if (solderPackInfo != null)
            return solderPackInfo.hasSolder();
        if (platformPackInfo != null)
            return platformPackInfo.hasSolder();
        return false;
    }
}
