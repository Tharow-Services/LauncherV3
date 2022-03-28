

package net.tharow.tantalum.platform.packsources;

import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.platform.io.FeedItem;
import net.tharow.tantalum.platform.io.SearchResult;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.rest.io.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultPackInfo implements PackInfo {
    private final SearchResult result;

    public SearchResultPackInfo(SearchResult result) {
        this.result = result;
    }



    @Override
    public String getName() {
        return result.getSlug();
    }

    @Override
    public String getDisplayName() {
        return result.getDisplayName();
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
        return null;
    }

    @Override
    public String getLatest() {
        return null;
    }

    @Override
    public List<String> getBuilds() {
        return Collections.emptyList();
    }

    @Override
    public ArrayList<FeedItem> getFeed() {
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Integer getRuns() {
        return null;
    }

    @Override
    public Integer getDownloads() {
        return null;
    }

    @Override
    public Integer getLikes() {
        return null;
    }

    @Override
    public boolean isServerPack() { return false; }

    @Override
    public Modpack getModpack(String build) throws BuildInaccessibleException {
        throw new BuildInaccessibleException(getDisplayName(), build);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isOfficial() { return false; }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean hasSolder() {
        return false;
    }
}
