

package net.tharow.tantalum.solder;

import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.solder.io.SolderPackInfo;

public interface ISolderPackApi {
    String getMirrorUrl();

    SolderPackInfo getPackInfoForBulk() throws RestfulAPIException;

    SolderPackInfo getPackInfo() throws RestfulAPIException;

    Modpack getPackBuild(String build) throws BuildInaccessibleException;
}
