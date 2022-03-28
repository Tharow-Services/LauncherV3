

package net.tharow.tantalum.launchercore.modpacks.sources;

import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.rest.io.PackInfo;

public interface IAuthoritativePackSource {

    PackInfo getPackInfo(InstalledPack pack);

    PackInfo getCompletePackInfo(PackInfo pack);
}
