

package net.tharow.tantalum.launchercore.modpacks.sources;

import net.tharow.tantalum.rest.io.PackInfo;

import java.util.Collection;

public interface IPackSource {
    String getSourceName();

    Collection<PackInfo> getPublicPacks();

    int getPriority(PackInfo packInfo);
}
