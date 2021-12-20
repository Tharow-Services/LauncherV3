package net.tharow.tantalum.platform;

import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.rest.io.PackInfo;

import java.util.Collection;

public class PlatformPackSource implements IPackSource {
    @Override
    public String getSourceName() {
        return "Platform Packsource";
    }

    @Override
    public Collection<PackInfo> getPublicPacks() {
        return null;
    }

    @Override
    public int getPriority(PackInfo packInfo) {
        return 0;
    }
}
