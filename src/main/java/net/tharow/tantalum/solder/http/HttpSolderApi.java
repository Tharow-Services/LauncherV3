

package net.tharow.tantalum.solder.http;

import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderInfo;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.FullModpacks;
import net.tharow.tantalum.solder.io.Solder;
import net.tharow.tantalum.solder.io.SolderInfo;
import net.tharow.tantalum.solder.io.SolderPackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HttpSolderApi implements ISolderApi {
    private final String clientId;
    private final Map<String, String> mirrorUrls = new HashMap<>();

    public HttpSolderApi(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public ISolderInfo getSolderInfo(String solderRoot) throws RestfulAPIException {
        return RestObject.getRestObject(SolderInfo.class, solderRoot);
    }

    @Override
    public ISolderPackApi getSolderPack(String solderRoot, String modpackSlug, String mirrorUrl) throws RestfulAPIException {
        return new HttpSolderPackApi(solderRoot, modpackSlug, clientId, mirrorUrl);
    }

    @Override
    public Collection<SolderPackInfo> getPublicSolderPacks(String solderRoot) throws RestfulAPIException {
        return internalGetPublicSolderPacks(solderRoot, this);
    }

    public String getMirrorUrl(String solderRoot) throws RestfulAPIException {
        synchronized (mirrorUrls) {
            if (!mirrorUrls.containsKey(solderRoot)) {
                String allPacksUrl = solderRoot + "modpack";
                Solder solder = RestObject.getRestObject(Solder.class, allPacksUrl);
                mirrorUrls.put(solderRoot, solder.getMirrorUrl());
            }

            return mirrorUrls.get(solderRoot);
        }
    }

    @Override
    public Collection<SolderPackInfo> internalGetPublicSolderPacks(String solderRoot, ISolderApi packFactory) throws RestfulAPIException {
        LinkedList<SolderPackInfo> allPackApis = new LinkedList<>();
        String allPacksUrl = solderRoot + "modpack/full.html?include=full&cid=" + clientId;

        FullModpacks technic = RestObject.getRestObject(FullModpacks.class, allPacksUrl);
        for (SolderPackInfo info : technic.getModpacks().values()) {
            ISolderPackApi solder = packFactory.getSolderPack(solderRoot, info.getName(), technic.getMirrorUrl());
            info.setSolder(solder);
            allPackApis.add(info);
        }

        return allPackApis;
    }
}
