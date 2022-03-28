

package net.tharow.tantalum.launchercore.modpacks.sources;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;

public interface IModpackTagBuilder {
    Iterable<String> getModpackTags(ModpackModel modpack);
}
