package net.tharow.tantalum.github.io;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.platform.io.INewsData;
import net.tharow.tantalum.platform.io.NewsArticle;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RepoReleasesData extends RestObject implements INewsData {
    @SerializedName("") private final ArrayList<RepoRelease>  releases = new ArrayList<>();

    public ArrayList<RepoRelease> getReleases() {
        return releases;
    }

    @Override
    public ArrayList<NewsArticle> getArticles() {
        ArrayList<NewsArticle> articles = new ArrayList<>();
        releases.forEach(repoRelease -> articles.add(repoRelease.toNewArticle()));
        return articles;
    }
}
