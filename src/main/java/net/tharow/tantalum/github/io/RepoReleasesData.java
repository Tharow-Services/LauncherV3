package net.tharow.tantalum.github.io;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
    private static final Gson gson = new Gson();
    private final ArrayList<RepoRelease>  releases = new ArrayList<>();

    public ArrayList<RepoRelease> getReleases() {
        return releases;
    }

    @Override
    public ArrayList<NewsArticle> getArticles() {
        ArrayList<NewsArticle> articles = new ArrayList<>();
        releases.forEach(repoRelease -> articles.add(repoRelease.toNewArticle()));
        return articles;
    }


    public static RepoReleasesData getRestObject(String url) throws RestfulAPIException {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", TantalumConstants.getUserAgent());
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            try (InputStream stream = conn.getInputStream()) {
                String data = IOUtils.toString(stream, StandardCharsets.UTF_8);
                //Time to Do some mystic magic//
                data = "{\"releases\":" + data + "}";
                RepoReleasesData result = gson.fromJson(data, RepoReleasesData.class);

                if (result == null) {
                    throw new RestfulAPIException("Unable to access URL [" + url + "]");
                }

                if (result.hasError()) {
                    throw new RestfulAPIException("Error in response: " + result.getError());
                }

                return result;
            }
        } catch (SocketTimeoutException e) {
            throw new RestfulAPIException("Timed out accessing URL [" + url + "]", e);
        } catch (MalformedURLException e) {
            throw new RestfulAPIException("Invalid URL [" + url + "]", e);
        } catch (JsonParseException e) {
            throw new RestfulAPIException("Error parsing response JSON at URL [" + url + "]", e);
        } catch (IOException e) {
            throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
        }
    }
}
