

package net.tharow.tantalum.launchercore.modpacks.sources;

import net.tharow.tantalum.launchercore.modpacks.InstalledPack;

import java.util.List;
import java.util.Map;

public interface IInstalledPackRepository {
    Map<String, InstalledPack> getInstalledPacks();

    List<String> getPackNames();

    String getSelectedSlug();

    void setSelectedSlug(String slug);

    InstalledPack put(InstalledPack installedPack);

    InstalledPack remove(String name);

    void save();
}
