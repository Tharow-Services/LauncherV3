

package net.tharow.tantalum.launchercore.image.face;

import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.image.IImageStore;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CrafatarFaceImageStore implements IImageStore<IUserType> {
    private final String mBaseUrl;

    public CrafatarFaceImageStore(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Override
    public boolean canDownloadImage(IUserType user, File location) {
        return true;
    }

    @Override
    public void downloadImage(IUserType user, File location) {
        try {
            Utils.downloadFile(mBaseUrl + "avatars/" + user.getId() + "?size=100&overlay", user.getDisplayName(), location.getAbsolutePath());
        } catch (InterruptedException ex) {
            //User cancelled
        } catch (IOException e) {
            Utils.getLogger().log(Level.INFO, "Error downloading user face image: " + user.getDisplayName(), e);
        }
    }

    @Override
    public String getJobKey(IUserType key) {
        return "user-face-" + key.getDisplayName();
    }

    @Override
    public boolean canRetry(IUserType key) {
        return false;
    }
}
