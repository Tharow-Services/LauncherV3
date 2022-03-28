

package net.tharow.tantalum.platform.packsources;

import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.PlatformPackInfoRepository;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.ISolderApi;

import java.util.ArrayList;
import java.util.Collection;

public class SinglePlatformSource extends PlatformPackInfoRepository implements IPackSource {
    private final String slug;

    public SinglePlatformSource(IPlatformApi platformApi, ISolderApi solderApi, String slug) {
        super(platformApi, solderApi);
        this.slug = slug;
    }

    @Override
    public String getSourceName() {
        return "Platform pack with slug '" + this.slug + "'";
    }

    @Override
    public Collection<PackInfo> getPublicPacks() {
        ArrayList<PackInfo> packs = new ArrayList<>(1);
        PackInfo info = getPlatformPackInfo(slug);

        if (info != null) {
            packs.add(info);
        }

        return packs;
    }

    @Override
    public int getPriority(PackInfo packInfo) {
        return 0;
    }
}
