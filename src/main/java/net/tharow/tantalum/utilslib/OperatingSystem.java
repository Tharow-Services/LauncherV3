

package net.tharow.tantalum.utilslib;

import java.io.File;
import java.util.Locale;

public enum OperatingSystem {
    LINUX("linux", new String[]{"linux", "unix"}),
    WINDOWS("windows", new String[]{"win"}),
    OSX("osx", new String[]{"mac"}),
    UNKNOWN("unknown", new String[0]);

    private static OperatingSystem operatingSystem;
    private final String name;
    private final String[] aliases;

    OperatingSystem(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public static String getJavaDir() {
        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator;

        if (getOperatingSystem() == WINDOWS) {
            return path + "java.exe";
        }

        return path + "java";
    }

    public static OperatingSystem getOperatingSystem() {
        if (OperatingSystem.operatingSystem != null) {
            return OperatingSystem.operatingSystem;
        }

        //Always specify english when tolowercase/touppercasing values for comparison against well-known values
        //Prevents an issue with turkish users
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        for (OperatingSystem operatingSystem : values()) {
            for (String alias : operatingSystem.getAliases()) {
                if (osName.contains(alias)) {
                    OperatingSystem.operatingSystem = operatingSystem;
                    return operatingSystem;
                }
            }
        }

        return UNKNOWN;
    }

    public File getUserDirectoryForApp(String appName) {
        String userHome = System.getProperty("user.home", ".");

        switch (this) {
            case LINUX:
                return new File(userHome, "."+appName+"/");
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    return new File(applicationData, "."+appName+"/");
                } else {
                    return new File(userHome, "."+appName+"/");
                }
            case OSX:
                return new File(userHome, "Library/Application Support/" + appName);
            default:
                return new File(userHome, appName + "/");
        }
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }
}
