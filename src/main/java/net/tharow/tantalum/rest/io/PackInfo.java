

package net.tharow.tantalum.rest.io;


import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.platform.io.FeedItem;

import java.util.ArrayList;
import java.util.List;

public interface PackInfo {


    String getName();

    String getDisplayName();

    String getWebSite();

    Resource getIcon();

    Resource getBackground();

    Resource getLogo();

    String getRecommended();

    String getLatest();

    List<String> getBuilds();

    ArrayList<FeedItem> getFeed();

    String getDescription();

    Integer getRuns();

    Integer getDownloads();

    Integer getLikes();

    Modpack getModpack(String build) throws BuildInaccessibleException;

    boolean isComplete();

    boolean isLocal();

    boolean isServerPack();

    boolean isOfficial();

    boolean hasSolder();
}
