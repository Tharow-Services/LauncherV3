

package net.tharow.tantalum.rest.io;

import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class Modpack extends RestObject {
    private String minecraft;
    private String java;
    private String memory;
    private List<Mod> mods;

    public Modpack() {

    }

    public Modpack(PlatformPackInfo info) {
        minecraft = info.getGameVersion();
        mods = new ArrayList<>();
        Mod mod = new Mod(info.getName(), info.getRecommended(), info.getUrl(), "");
        mods.add(mod);
    }


    public String getGameVersion() {
        return minecraft;
    }

    public List<Mod> getMods() {
        return mods;
    }

    public String getJava() { return java; }
    public String getMemory() { return memory; }
}
