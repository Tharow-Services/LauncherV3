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
