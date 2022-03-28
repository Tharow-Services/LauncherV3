

package net.tharow.tantalum.launchercore.image.face;

import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.image.IImageStore;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

public class WebAvatarImageStore implements IImageStore<AuthorshipInfo> {

    public WebAvatarImageStore() {
    }

    @Override
    public boolean canDownloadImage(AuthorshipInfo key, File target) {
        return true;
    }

    @Override
    public void downloadImage(AuthorshipInfo key, File target) {
        try {
            //final String user = (key.getUser()!=null?key.getUser():"System");
            //final String url = (key.getAvatar()!=null?key.getAvatar():TantalumConstants.AVATAR_URL+user+".png");
            Utils.downloadFile(key.getAvatar().toLowerCase(Locale.ROOT), key.getUser(), target.getAbsolutePath());
        } catch (InterruptedException ex) {
            //User cancel
        } catch (IOException e) {
            Utils.getLogger().log(Level.INFO, "Error downloading user avatar: " + key.getUser(), e);
        }
    }

    @Override
    public String getJobKey(AuthorshipInfo key) {
        return "user-avatar-" + key.getUser();
    }

    @Override
    public boolean canRetry(AuthorshipInfo key) {
        return false;
    }
}
