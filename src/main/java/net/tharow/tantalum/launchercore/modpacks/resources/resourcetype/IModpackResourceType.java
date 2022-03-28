

package net.tharow.tantalum.launchercore.modpacks.resources.resourcetype;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Resource;

public interface IModpackResourceType {
    Resource getResource(ModpackModel modpack);

    String getImageName();
}
