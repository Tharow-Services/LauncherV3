

package net.tharow.tantalum.platform;

import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.packinfo.CombinedPackInfo;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.SolderPackInfo;
import net.tharow.tantalum.utilslib.Utils;

import java.util.logging.Level;

public class PlatformPackInfoRepository implements IAuthoritativePackSource {
    private final IPlatformApi platform;
    private final ISolderApi solder;

    public PlatformPackInfoRepository(IPlatformApi platform, ISolderApi solder) {
        this.platform = platform;
        this.solder = solder;
    }

    @Override
    public PackInfo getPackInfo(InstalledPack pack) {
        return getPlatformPackInfo(pack.getName());
    }

    @Override
    public PackInfo getCompletePackInfo(PackInfo pack) {
        return getPlatformPackInfo(pack.getName());
    }

    protected PackInfo getPlatformPackInfo(String slug) {
        try {
            PackInfo info = null;

            PlatformPackInfo platformInfo = platform.getPlatformPackInfoForBulk(slug);

            info = getInfoFromPlatformInfo(platformInfo);

            return info;
        } catch (RestfulAPIException ex) {
            Utils.getLogger().log(Level.WARNING, "Unable to load platform pack " + slug, ex);
            return null;
        }
    }

    protected PackInfo getInfoFromPlatformInfo(PlatformPackInfo platformInfo) {
        if (platformInfo != null && platformInfo.hasSolder()) {
            try {
                ISolderPackApi solderPack = solder.getSolderPack(platformInfo.getSolder(), platformInfo.getName(), solder.getMirrorUrl(platformInfo.getSolder()));
                SolderPackInfo solderInfo = solderPack.getPackInfoForBulk();

                if (solderInfo == null)
                    return platformInfo;
                else
                    return new CombinedPackInfo(solderInfo, platformInfo);
            } catch (RestfulAPIException ex) {
                ex.printStackTrace();
                return platformInfo;
            }
        } else {
            return platformInfo;
        }
    }
}
