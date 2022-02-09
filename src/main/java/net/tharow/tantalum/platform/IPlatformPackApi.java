package net.tharow.tantalum.platform;

import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestfulAPIException;

public interface IPlatformPackApi {
    PlatformPackInfo getPlatformPackInfoForBulk(String packSlug) throws RestfulAPIException;

    PlatformPackInfo getPlatformPackInfo(String packSlug) throws RestfulAPIException;
}
