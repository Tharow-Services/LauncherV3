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

package net.tharow.tantalum.launchercore;

import net.tharow.tantalum.autoupdate.IBuildNumber;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.utilslib.Utils;

import java.net.URL;

public class TantalumConstants {
    public static final String technicURL = "https://mirror.technicpack.net/Technic/";
    public static final String technicVersions = "https://tantalum.tharow.net/launcher/version/";
    public static final String technicFmlLibRepo = technicURL + "lib/fml/";
    public static final String technicForgeRepo = technicURL + "lib/";
    public static URL UpdateUrl = null;

    static {
        try {
            UpdateUrl = Utils.getFullUrl("https://tantalum-auth.azurewebsites.net/");
        } catch (DownloadException ignored) {}
    }

    private static IBuildNumber falseBuildNumber;
    private static IBuildNumber buildNumber;
    private static String userAgent;
    private static boolean isDebug;

    public static IBuildNumber getBuildNumber() {
        return buildNumber;
    }



    public static void setBuildNumber(IBuildNumber buildNumber) {
        TantalumConstants.buildNumber = buildNumber;

        userAgent = "Mozilla/5.0 (Java) TantalumLauncher/5.1." + buildNumber.getBuildNumber();
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static boolean isIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        TantalumConstants.isDebug = isDebug;
    }

    public static IBuildNumber getFalseBuildNumber() {
        return falseBuildNumber;
    }

    public static void setFalseBuildNumber(IBuildNumber falseBuildNumber) {
        TantalumConstants.falseBuildNumber = falseBuildNumber;
    }
}
