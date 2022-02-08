package net.tharow.tantalum.tantalum.io;

import com.google.gson.annotations.SerializedName;
import net.tharow.tantalum.autoupdate.IUpdateStream;
import net.tharow.tantalum.autoupdate.http.HttpUpdateStream;
import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.tantalum.RequiresAccessCode;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Platform extends RestObject implements IPlatformInfo {

    private static final String DEFAULT_VERSION = "stable4";

    private final String name;
    private final String version;
    private transient final String url;
    private transient final String accessCode;
    private transient final String accessVerb;
    private transient boolean offline = false;


    public Platform(String name, String version, String url, boolean offline){
        this.name = name;
        this.version = version;
        this.url = url;
        this.offline = offline;
        this.accessCode = null;
        this.accessVerb = null;
    }

    public Platform(String server, String accessCode, String accessVerb) throws RestfulAPIException, RequiresAccessCode{
        this.url = server;
        Platform platform = RestObject.getRestObject(Platform.class, server);
        this.name = platform.name;
        this.version = platform.version;
        this.accessCode = accessCode;
        this.accessVerb = accessVerb;
    }

    public Platform(String server) throws RestfulAPIException, RequiresAccessCode {
        this.url = server;
        Platform platform = RestObject.getRestObject(Platform.class, server);
        this.name = platform.name;
        this.version = platform.version;
        this.accessCode = null;
        this.accessVerb = null;
        final boolean requiresAccess = RestObject.getRestObject(null, url + "public").hasError();
        if (requiresAccess) throw new RequiresAccessCode("Platform Requires Access Code");
    }

    public Platform(@NotNull IPlatformInfo platform){
        this(platform.getName(),platform.getVersion(),platform.get(), platform.isOffline());
    }

    public Platform(){
        this.name = null;
        this.version = null;
        this.url = null;
        this.offline = true;
        this.accessCode = null;
        this.accessVerb = null;
    }

    public String getHost() {
        return Objects.requireNonNull(Utils.getUrl(url)).getHost();
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAccessCode() {
        return accessCode;
    }

    @Override
    public String getAccessVerb() {
        return accessVerb;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String get() {
        return url;
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    @Override
    public void refresh() {

    }

    @Override
    public String toString(){
        return name.replaceFirst(" API","");
    }
}
