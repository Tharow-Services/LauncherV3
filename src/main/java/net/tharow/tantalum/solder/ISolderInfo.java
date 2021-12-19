package net.tharow.tantalum.solder;

import net.tharow.tantalum.rest.RestfulAPIException;

public interface ISolderInfo {
    String getApi();
    String getVersion();
    String getStream();

}

