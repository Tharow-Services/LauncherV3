package net.tharow.tantalum.github.io;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class RepoReleaseAuthor {
    private String login;
    private int id;
    @SerializedName("node_id") private String nodeId;
    @SerializedName("avatar_url") private String avatarUrl;
    @SerializedName("gravatar_url") private String gravatarId;
    private String url;
    @SerializedName("html_url") private String htmlUrl;
    @SerializedName("followers_url") private String followersUrl;
    @SerializedName("following_url") private String followingUrl;
    @SerializedName("gists_url") private String gistsUrl;
    @SerializedName("starred_url") private String starredUrl;
    @SerializedName("subscriptions_url") private String subscriptionsUrl;
    @SerializedName("organizations_url") private String organizationsUrl;
    @SerializedName("repos_url") private String reposUrl;
    @SerializedName("events_url") private String eventsUrl;
    @SerializedName("received_events_url") private String receivedEventsUrl;
    private String type;
    @SerializedName("site_admin") private boolean siteAdmin;


    public String getLogin() {
        return login;
    }


    public int getId() {
        return id;
    }


    public String getNodeId() {
        return nodeId;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }


    public String getGravatarId() {
        return gravatarId;
    }


    public String getUrl() {
        return url;
    }


    public String getHtmlUrl() {
        return htmlUrl;
    }


    public String getFollowersUrl() {
        return followersUrl;
    }


    public String getFollowingUrl() {
        return followingUrl;
    }

    public String getGistsUrl() {
        return gistsUrl;
    }

    public String getStarredUrl() {
        return starredUrl;
    }

    public String getSubscriptionsUrl() {
        return subscriptionsUrl;
    }

    public String getOrganizationsUrl() {
        return organizationsUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public String getReceivedEventsUrl() {
        return receivedEventsUrl;
    }

    public String getType() {
        return type;
    }

    public boolean isSiteAdmin() {
        return siteAdmin;
    }
}
