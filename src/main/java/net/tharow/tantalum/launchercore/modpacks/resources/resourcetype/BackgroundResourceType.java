

package net.tharow.tantalum.launchercore.modpacks.resources.resourcetype;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Resource;

public class BackgroundResourceType implements IModpackResourceType {
    @Override
    public Resource getResource(ModpackModel modpack) {
        return modpack.getBackground();
    }

    @Override
    public String getImageName() {
        return "background.png";
    }
}
