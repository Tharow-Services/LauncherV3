package net.tharow.tantalum.tantalum.io;

import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.tantalum.RequiresAccessCode;
import net.tharow.tantalum.utilslib.UUID5;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Platform extends RestObject implements IPlatformInfo {

    private static final String DEFAULT_VERSION = "stable4";

    private final String name;
    private final String version;
    private final String url;
    private final String accessCode;
    private final String accessVerb;
    private transient boolean offline = false;


    public Platform(String name, String version, String url, boolean offline){
        this.name = name;
        this.version = version;
        this.url = url;
        this.offline = offline;
        this.accessCode = null;
        this.accessVerb = null;
    }

    public Platform(String server, String accessVerb, String accessCode) throws RestfulAPIException, RequiresAccessCode{
        this.url = server;
        Platform platform = RestObject.getRestObject(Platform.class, server);
        this.name = platform.name;
        this.version = platform.version;
        this.accessCode = accessCode;
        this.accessVerb = accessVerb;
        test();
    }

    public Platform(String server) throws RestfulAPIException, RequiresAccessCode {
        this.url = server;
        Platform platform = RestObject.getRestObject(Platform.class, server);
        this.name = platform.name;
        this.version = platform.version;
        this.accessCode = null;
        this.accessVerb = null;
        test();
    }
    private void test() throws RestfulAPIException, RequiresAccessCode {
        test(this);
    }

    private static void test(Platform platform) throws RestfulAPIException, RequiresAccessCode {
        RestObject restObject;
        if (platform.accessVerb == null){
            restObject = RestObject.getRestObject(RestObject.class, platform.url + "news");
        } else {
            restObject = RestObject.getRestObject(RestObject.class, platform.url + "news?"+platform.accessVerb+'='+platform.accessCode);
        }
        if (restObject.hasError()) throw new RequiresAccessCode("Accessing News Api For Platform: "+platform.name+ " With Error: "+ restObject.getError());

    }

    public void refresh(){
        try {
            test(this);
        } catch (RestfulAPIException | RequiresAccessCode e) {
            Utils.getLogger().warning("Couldn't Contact Platform Setting to Offline Mode");
            this.offline = true;
            e.printStackTrace();
        }
    }

    public static Platform refresh(@NotNull Platform platform){
        Platform toReturn;
        try {
            test(platform);
            toReturn = platform;
        } catch (RestfulAPIException | RequiresAccessCode e) {
            Utils.getLogger().warning("Couldn't Contact Platform Setting to Offline Mode");
            toReturn = platform;
            toReturn.offline = true;
            e.printStackTrace();
        }
        return toReturn;
    }

    public Platform(){
        this.name = null;
        this.version = null;
        this.url = null;
        this.offline = true;
        this.accessCode = null;
        this.accessVerb = null;
    }

    @Override
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
    public UUID get() {
        return UUID5.fromUTF8(url);
    }

    @Override
    public boolean isOffline() {
        return offline;
    }

    @Override
    public String toString() {
        return getName().replaceFirst(" API", "");
    }
}
