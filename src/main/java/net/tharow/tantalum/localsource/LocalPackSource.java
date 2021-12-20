package net.tharow.tantalum.localsource;

import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.rest.io.PackInfo;

public class LocalPackSource implements IAuthoritativePackSource {
    @Override
    public PackInfo getPackInfo(InstalledPack pack) {
        return null;
    }

    @Override
    public PackInfo getCompletePackInfo(PackInfo pack) {
        return null;
    }
}
