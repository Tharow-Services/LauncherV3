package net.tharow.tantalum.github;

import net.tharow.tantalum.github.io.RepoReleaseAssetsData;
import net.tharow.tantalum.github.io.RepoReleaseAuthor;

import java.util.Date;

public interface IRepoRelease {
    //Start of raw Access Methods//
    String getApiUrl();

    String getAssetsUrl();

    String getUploadUrl();

    String getHtmlUrl();

    int getRawId();

    RepoReleaseAuthor getAuthor();

    String getNodeId();

    String getTagName();

    String getTargetCommitBranch();

    String getName();

    boolean isDraft();

    boolean isPreRelease();

    Date getCreatedAt();

    Date getPublishedAt();

    // TODO: 12/20/2021 Add Release Asset Support
    RepoReleaseAssetsData getAssets();

    String getTarballUrl();

    String getZipballUrl();

    String getBody();
}
