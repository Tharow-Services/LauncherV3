package net.tharow.tantalum.github.io;

import com.google.gson.annotations.SerializedName;
import net.tharow.tantalum.github.IRepoRelease;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.platform.io.INewsArticle;
import net.tharow.tantalum.platform.io.NewsArticle;
import net.tharow.tantalum.platform.io.NewsData;

import java.util.Date;

@SuppressWarnings("unused")
public class RepoRelease implements IRepoRelease, INewsArticle {
    private String url;
    @SerializedName("assets_url") private String assetsUrl;
    @SerializedName("upload_url") private String uploadUrl;
    @SerializedName("html_url") private String htmlUrl;
    private int id;
    private RepoReleaseAuthor author;
    @SerializedName("node_id") private String nodeId;
    @SerializedName("tag_name") private String tagName;
    @SerializedName("target_commitish") private String targetCommitBranch;
    private String name;
    private boolean draft;
    @SerializedName("prerelease") private boolean preRelease;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("published_at") private Date publishedAt;
    // TODO: 12/20/2021 Add Release Asset Support
    //private RepoReleaseAssetsData assets;
    @SerializedName("tarball_url") private String tarballUrl;
    @SerializedName("zipball_url") private String zipballUrl;
    private String body;

    //Turn this into News//
    public NewsArticle toNewArticle(){
        return new NewsArticle(this);
    }


    //Start of News Access Methods//
    @Override
    public int getId() {
        return this.getRawId();
    }

    @Override
    public String getUsername() {
        return this.getAuthor().getLogin();
    }

    @Override
    public String getAvatar() {
        return this.getAuthor().getAvatarUrl();
    }

    @Override
    public String getTitle() {
        return this.getName();
    }

    @Override
    public String getContent() {
        return getBody();
    }

    @Override
    public Date getDate() {
        return getPublishedAt();
    }

    @Override
    public AuthorshipInfo getAuthorshipInfo() {
        return new AuthorshipInfo(this.getUsername(),this.getAvatar(),this.getDate());
    }

    @Override
    public String getUrl() {
        return getHtmlUrl();
    }

    //Start of Raw Access Methods//
    @Override
    public String getApiUrl() {
        return url;
    }

    @Override
    public String getAssetsUrl() {
        return assetsUrl;
    }

    @Override
    public String getUploadUrl() {
        return uploadUrl;
    }

    @Override
    public String getHtmlUrl() {
        return htmlUrl;
    }

    @Override
    public int getRawId() {
        return id;
    }

    @Override
    public RepoReleaseAuthor getAuthor() {
        return author;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public String getTargetCommitBranch() {
        return targetCommitBranch;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDraft() {
        return draft;
    }

    @Override
    public boolean isPreRelease() {
        return preRelease;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getPublishedAt() {
        return publishedAt;
    }

    // TODO: 12/20/2021 Add Release Asset Support
    @Override
    public RepoReleaseAssetsData getAssets() {
        //return assets;
        return null;
    }


    @Override
    public String getTarballUrl() {
        return tarballUrl;
    }

    @Override
    public String getZipballUrl() {
        return zipballUrl;
    }

    @Override
    public String getBody() {
        return body;
    }
}
