/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    private IPlatformApi platform;
    private ISolderApi solder;

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

    protected PackInfo getInfoFromPlatformInfo(PlatformPackInfo platformInfo) throws RestfulAPIException {
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
