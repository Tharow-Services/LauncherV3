package net.tharow.tantalum.platform.io;

import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.utilslib.Utils;

public class PlatformInfo extends RestObject implements IPlatformInfo {
    private String name;
    private String version;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBuild() {
        return 0;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "PlatformInfo{" +
                "name='" + name + '\'' +
                ", version='" + version +
                '\''+'}';
    }

    public void toConsole(String platformUrl){
        Utils.logDebug("Platform Info From: "+platformUrl);
        Utils.logDebug("Platform Name: " + getName());
        Utils.logDebug("Platform Version: " + getVersion());
    }
}
