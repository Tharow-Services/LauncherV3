

package net.tharow.tantalum.launchercore.modpacks.resources;

import net.tharow.tantalum.launchercore.image.IImageStore;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.IModpackResourceType;
import net.tharow.tantalum.rest.io.Resource;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PackImageStore implements IImageStore<ModpackModel> {
    private final IModpackResourceType resourceType;

    public PackImageStore(IModpackResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean canDownloadImage(ModpackModel key, File target) {
        Resource res = resourceType.getResource(key);
        return res != null && res.getUrl() != null && !res.getUrl().isEmpty();
    }

    @Override
    public void downloadImage(ModpackModel key, File target) {
        Resource res = resourceType.getResource(key);

        if (res == null || res.getUrl() == null || res.getUrl().isEmpty())
            return;

        try {
            // We can pass null as the name here because there's no listener set, so the name is never used
            Utils.downloadFile(res.getUrl(), null, target.getAbsolutePath());
        } catch (InterruptedException ex) {
            //user cancel
        } catch (IOException e) {
            Utils.getLogger().log(Level.INFO, "Error downloading pack resource " + res.getUrl() + " for pack " + key.getName(), e);
        }
    }

    @Override
    public String getJobKey(ModpackModel key) {
        return "pack-resource-" + key.getName() + "-" + resourceType.getImageName();
    }

    @Override
    public boolean canRetry(ModpackModel key) {
        return (key.getPackInfo() == null || !key.getPackInfo().isComplete());
    }
}
