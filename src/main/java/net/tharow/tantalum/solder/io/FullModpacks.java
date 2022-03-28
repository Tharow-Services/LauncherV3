

package net.tharow.tantalum.solder.io;

import net.tharow.tantalum.rest.RestObject;

import java.util.LinkedHashMap;

@SuppressWarnings({"unused"})
public class FullModpacks extends RestObject {

    private LinkedHashMap<String, SolderPackInfo> modpacks;
    private String mirror_url;

    public LinkedHashMap<String, SolderPackInfo> getModpacks() {
        return modpacks;
    }

    public String getMirrorUrl() {
        return mirror_url;
    }
}
