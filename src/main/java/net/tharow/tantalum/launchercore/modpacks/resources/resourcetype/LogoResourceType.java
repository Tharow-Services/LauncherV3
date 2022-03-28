

package net.tharow.tantalum.launchercore.modpacks.resources.resourcetype;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.Resource;

public class LogoResourceType implements IModpackResourceType {
    @Override
    public Resource getResource(ModpackModel modpack) {
        return modpack.getLogo();
    }

    @Override
    public String getImageName() {
        return "logo.png";
    }
}
