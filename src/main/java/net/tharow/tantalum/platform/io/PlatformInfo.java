package net.tharow.tantalum.platform.io;

import net.tharow.tantalum.platform.IPlatformInfo;
import net.tharow.tantalum.rest.RestObject;

public class PlatformInfo extends RestObject implements IPlatformInfo {
    private String name;
    private String version;

    @Override
    public String getName() {
        return name;
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
}
