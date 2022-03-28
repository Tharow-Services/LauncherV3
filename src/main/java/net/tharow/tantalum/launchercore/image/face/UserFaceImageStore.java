

package net.tharow.tantalum.launchercore.image.face;

import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.image.IImageStore;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class UserFaceImageStore implements IImageStore<IUserType> {

    @Override
    public boolean canDownloadImage(IUserType user, File location) {
        return true;
    }

    @Override
    public void downloadImage(IUserType user, File location) {
        //Utils.getLogger().log(Level.INFO, "User Face Image Isn't Currently Implemented");
        try {
            Utils.downloadFile(TantalumConstants.USER_AVATAR_URL + user.getDisplayName() + ".png", user.getDisplayName(), location.getAbsolutePath());
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
