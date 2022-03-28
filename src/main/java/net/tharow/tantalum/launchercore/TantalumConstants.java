

package net.tharow.tantalum.launchercore;

import lombok.Getter;
import lombok.Setter;
import net.tharow.tantalum.autoupdate.IBuildNumber;

public class TantalumConstants {
    public static final String MIRROR_URL = "https://raw.githubusercontent.com/Tharow-Services/Tantalum-Mirror/main/";
    public static final String VERSIONS_URL = MIRROR_URL+ "version/";
    public static final String MAVEN_URL = MIRROR_URL+ "maven/";
    public static final String FORGE_REPO_URL = MAVEN_URL + "lib/";
    public static final String FML_REPO_URL = FORGE_REPO_URL + "fml/";
    public static final String USER_AVATAR_URL = MIRROR_URL+ "assets/avatars/";
    public static final String AVATAR_URL = USER_AVATAR_URL + "gravitar/";
    public static final String UPDATE_URL = MIRROR_URL+ "stable4.json";

    public static final String TANTALUM_AUTH_URL = "https://tantalum-auth.azurewebsites.net/";
    public static final String TANTALUM_AUTH_PLATFORM_URL = TANTALUM_AUTH_URL + "platform/";

    public static final String DISCOVER_URL = "https://tantalum.tharow.net/discover.html";

    public static final String technicURL = MIRROR_URL;
    //public static final String technicVersions = "version/";
    //public static final String technicFmlLibRepo = technicURL + "lib/fml/";
    //public static final String technicForgeRepo = technicURL + "lib/";

    public static final String forgeMavenRepo = MAVEN_URL;//"https://files.minecraftforge.net/maven/";
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
    @Getter @Setter
    private static boolean unlocked = false;
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
