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
    public static final String MIRROR_URL = "https://raw.githubusercontent.com/Tharow-Services/Tantalum-Mirror/main/";
    public static final String VERSIONS_URL = MIRROR_URL+ "version/";
    public static final String MAVEN_URL = MIRROR_URL+ "maven/";
    public static final String MC_REPO_URL = MAVEN_URL + "lib/";
    public static final String FML_REPO_URL = MC_REPO_URL + "fml/";
    public static final String USER_AVATAR_URL = MIRROR_URL+ "assets/avatars/";
    public static final String AVATAR_URL = USER_AVATAR_URL + "gravitar/";
    public static final String UPDATE_URL = MIRROR_URL+ "stable4.json";

    public static final String TANTALUM_AUTH_URL = "https://tantalum-auth.azurewebsites.net/";
    public static final String TANTALUM_AUTH_PLATFORM_URL = TANTALUM_AUTH_URL + "platform/";

    public static final String technicURL = MIRROR_URL;
    public static final String technicVersions = "version/";
    public static final String technicFmlLibRepo = technicURL + "lib/fml/";
    public static final String technicForgeRepo = technicURL + "lib/";

    public static final String forgeMavenRepo = "https://files.minecraftforge.net/maven/";
    public static final String JAVA_RUNTIMES = MIRROR_URL+ "runtimes/all.json";
    public static final String MINECRAFT_ASSETS = MIRROR_URL + "assets/objects/";
    public static final String TRACKING_URL = "https://tantalum-auth.azurewebsites.net/tracking";
    public static final String NEWS_URL = "https://api.github.com/repos/Tharow-Services/Tantalum-Launcher/releases";

    public static final String FakeNullUrl = "about:blank";

    public static final String SYSTEM_USERNAME = "System";

    public static final String[] FALLBACK_MIRROR = {
            "https://libraries.minecraft.net/", // Default Minecraft Mirror
            "https://files.minecraftforge.net/maven/", // Default Forge Mirror
            "https://mirror.technicpack.net/Technic/lib/", // Technic Platform Mirror
            "https://raw.githubusercontent.com/Tharow-Services/Technic/master/lib/", // Alt Technic Mirror
            MAVEN_URL,
            "https://download.mcbbs.net/maven/",
    };

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
}
