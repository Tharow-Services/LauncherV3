

package net.tharow.tantalum.solder;

import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.solder.io.SolderPackInfo;

import java.util.Collection;

public interface ISolderApi {
    ISolderInfo getSolderInfo(String solderRoot) throws RestfulAPIException;

    ISolderPackApi getSolderPack(String solderRoot, String modpackSlug, String mirrorUrl) throws RestfulAPIException;

    Collection<SolderPackInfo> getPublicSolderPacks(String solderRoot) throws RestfulAPIException;

    String getMirrorUrl(String solderRoot) throws RestfulAPIException;

    Collection<SolderPackInfo> internalGetPublicSolderPacks(String solderRoot, ISolderApi packFactory) throws RestfulAPIException;
}
