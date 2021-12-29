package net.tharow.tantalum.launcher.io;

import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Platform implements IPlatformInfo {

    private final String name;
    private final int build;
    private final String version;
    private final String url;

    Platform(){
        this.name = "NAME";
        this.build = -1;
        this.version = "VERSION";
        this.url = "URL";
    }

    Platform(String name,String version, String url, int build){
        this.name = name;
        this.build = build;
        this.version = version;
        this.url = url;
    }

    Platform(@NotNull IPlatformInfo platformInfo, String url, int build){
        this.name = platformInfo.getName();
        this.version = platformInfo.getVersion();
        this.build = build;
        this.url = url;
    }

    Platform(@NotNull IPlatformInfo platformInfo, String url){
        this.name = platformInfo.getName();
        this.version = platformInfo.getVersion();
        this.build = 800;
        this.url = url;
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
    public int getBuild(){return build;}

    @Override
    public String getVersion() {
        return version;
    }

}
