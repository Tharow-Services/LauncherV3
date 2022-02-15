/*
 * This file is part of Technic Launcher.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.utilslib;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.tharow.tantalum.authlib.AuthlibAuthenticator;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.logging.Level;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.launchercore.mirror.download.Download;
import net.tharow.tantalum.launchercore.util.DownloadListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;



public class Utils {
    private static final Logger logger = Logger.getLogger("Launcher");
    private static final int DOWNLOAD_RETRIES = 3;
    @SuppressWarnings("unused")
    public static volatile Object ignored;

    private static final Gson gson;
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setExclusionStrategies(new AnnotationExclusionStrategy(AuthlibAuthenticator.class));
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC, Modifier.PROTECTED);
        builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());

        gson = builder.create();

        // Make sure we're logging everything we want to be logging
    }

    public static String urlEncoder(final String input){
        String string = null;
        try{string = URLEncoder.encode(input, "utf-8");}
        catch (UnsupportedEncodingException ignored){}
        return string;
    }

    public static void noOperation(){}

    public static @NotNull UUID getUUID(String name){
        return UUID5.fromUTF8(null, name);
    }

    public static Date getDate(String date){
        return getDateFromPattern("yyyy-MM-dd'T'HH:mm:ssZ",date);
    }

    public static Date getDateFromPattern(String datePattern, String date){
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date result;
        try {
            result = dateFormat.parse(date);
        } catch (ParseException e) {
            Utils.getLogger().warning("Unable to get date from: " + date + " With Date Pattern: " + datePattern);
            result = Calendar.getInstance().getTime();
        }
        return result;
    }

    public static Gson getGson() {
        return gson;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void logDebug(String msg){
        getLogger().log(Level.DEBUG,msg);
    }

    public static String urlEncode(String string){
        try {return URLEncoder.encode(string, "utf-8");}
        catch (UnsupportedEncodingException ignored) {}
        return URLEncoder.encode(string);
    }

    /**
     * Establishes an HttpURLConnection from a URL, with the correct configuration to receive content from the given URL.
     *
     * @param url The URL to set up and receive content from
     * @return A valid HttpURLConnection
     * @throws IOException The openConnection() method throws an IOException and the calling method is responsible for handling it.
     */
    public static HttpURLConnection openHttpConnection(URL url) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(false);
        System.setProperty("http.agent", TantalumConstants.getUserAgent());
        conn.setRequestProperty("User-Agent", TantalumConstants.getUserAgent());
        conn.setUseCaches(false);
        return conn;
    }

    /**
     * Opens an HTTP connection to a web URL and tests that the response is a valid 200-level code
     * and we can successfully open a stream to the content.
     *
     * @param urlLoc The HTTP URL indicating the location of the content.
     * @return True if the content can be accessed successfully, false otherwise.
     */
    public static boolean pingHttpURL(String urlLoc) {
        try {
            final URL url = getFullUrl(urlLoc);
            HttpURLConnection conn = openHttpConnection(url);
            conn.setRequestMethod("HEAD");

            int responseCode = conn.getResponseCode();
            int responseFamily = responseCode / 100;
            //System.out.println(responseCode + " " + urlLoc);
            if (responseFamily == 3) {
                String newUrl = conn.getHeaderField("Location");
                URL redirectUrl = null;
                try {
                    redirectUrl = new URL(newUrl);
                } catch (MalformedURLException ex) {
                    throw new DownloadException("Invalid Redirect URL: " + url, ex);
                }

                conn = openHttpConnection(redirectUrl);
                responseCode = conn.getResponseCode();
                responseFamily = responseCode/100;
            }

            if (responseFamily == 2) {
                try (InputStream stream = conn.getInputStream()) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Got an error when pinging " + urlLoc, ex);
            return false;
        }
    }

    public static boolean sendTracking(String category, String action, String label, String clientId) {
        getLogger().log(Level.TRACKING,"Sending Tracking Command: Category: "+category+" Action: "+action+" Label: "+label+" Client ID: "+clientId);
        final Tracking tracking = new Tracking(category, action, label, clientId);
        final String data = gson.toJson(tracking);
        try {
            postJson(TantalumConstants.TRACKING_URL, data);
            return true;
        } catch (IOException e) {
            getLogger().log(Level.TRACKING, "Unable to send tracking command");
            return false;
        }
    }

    /**
     *
     * Run a command on the local command line and return the program output.
     * THIS COMMAND IS BLOCKING!  Only run for short command line stuff, or I guess run on a thread.
     *
     * @param command List of args to run on the command line
     * @return The newline-separated program output
     */
    public static String getProcessOutput(String... command) {
        String out = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            final StringBuilder response=new StringBuilder();

            new Thread(() -> {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                } catch (IOException ex) {
                    //Don't let other process' problems concern us
                }
            }).start();
            process.waitFor();


            if (response.toString().length() > 0) {
                out = response.toString().trim();
            }
        }
        catch (IOException | InterruptedException e) {
            //Some kind of problem running java -version or getting output, just assume the version is bad
            return null;
        } //Something booted us while we were waiting on java -version to complete, just assume
        //this version is bad

        return out;
    }

    public static URL getUrl(String url){
        try {
            return getFullUrl(url);
        } catch (DownloadException e) {
            getLogger().severe("Invalid Url: " + url);
        }
        return null;
    }

    public static URL getFullUrl(String url) throws DownloadException {
        URL urlObject;

        try {
            urlObject = new URL(url);
        } catch (MalformedURLException ex) {
            throw new DownloadException("Invalid URL: " + url, ex);
        }

        return urlObject;
    }

    public static String getETag(String address) {
        String md5 = "";

        try {
            URL url = getFullUrl(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            System.setProperty("http.agent", TantalumConstants.getUserAgent());
            conn.setRequestProperty("User-Agent", TantalumConstants.getUserAgent());
            HttpURLConnection.setFollowRedirects(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);

            String eTag = conn.getHeaderField("ETag");
            if (eTag != null) {
                eTag = eTag.replaceAll("^\"|\"$", "");
                if (eTag.length() == 32) {
                    md5 = eTag;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return md5;
    }

    public static Download downloadFile(String url, String name, String output, File cache, IFileVerifier verifier, DownloadListener listener) throws IOException, InterruptedException {
        int tries = DOWNLOAD_RETRIES;
        File outputFile = null;
        Download download = null;
        while (tries > 0) {
            getLogger().info("Starting download of " + url + ", with " + tries + " tries remaining");
            tries--;
            download = new Download(getFullUrl(url), name, output);
            download.setListener(listener);
            download.run();
            if (download.getResult() != Download.Result.SUCCESS) {
                if (download.getOutFile() != null) {
                    download.getOutFile().delete();
                }

                if (Thread.interrupted())
                    throw new InterruptedException();

                System.err.println("Download of " + url + " Failed!");
                if (listener != null) {
                    listener.stateChanged("Download failed, retries remaining: " + tries, 0F);
                }
            } else {
                if (download.getOutFile().exists() && (verifier == null || verifier.isFileValid(download.getOutFile()))) {
                    outputFile = download.getOutFile();
                    break;
                }
            }
        }
        if (outputFile == null) {
            throw new DownloadException("Failed to download " + url, download != null ? download.getException() : null);
        }
        if (cache != null) {
            FileUtils.copyFile(outputFile, cache);
        }
        return download;
    }

    public static Download downloadFile(String url, String name, String output, File cache) throws IOException, InterruptedException {
        return downloadFile(url, name, output, cache, null, null);
    }

    public static Download downloadFile(String url, String name, String output) throws IOException, InterruptedException {
        return downloadFile(url, name, output, null);
    }

    public static String postJson(String url, String data) throws IOException {
        byte[] rawData = data.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(rawData.length));
        connection.setRequestProperty("Content-Language", "en-US");

        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(rawData);
        writer.flush();
        writer.close();

        InputStream stream = null;
        String returnable = null;
        try {
            stream = connection.getInputStream();
            returnable = IOUtils.toString(stream, Charsets.UTF_8);
        } catch (IOException e) {
            stream = connection.getErrorStream();

            if (stream == null) {
                throw e;
            }
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException ignored) {
            }
        }

        return returnable;
    }

    static class Tracking {
        private final String category;
        private final String action;
        private final String label;
        private final String clientId;

        public Tracking(String category, String action, String label, String clientId){
            this.category = category;
            this.action = action;
            this.label = label;
            this.clientId = clientId;
        }

        public String getCategory() {
            return category;
        }

        public String getAction() {
            return action;
        }

        public String getLabel() {
            return label;
        }

        public String getClientId() {
            return clientId;
        }
    }
}
