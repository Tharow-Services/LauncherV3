

package net.tharow.tantalum.launchercore.launch.java;

import net.tharow.tantalum.launchercore.launch.java.version.CurrentJavaVersion;
import net.tharow.tantalum.utilslib.OperatingSystem;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Represents a repository of all the versions of java available to launch games with.
 */
public class JavaVersionRepository {
    private final Map<File, IJavaVersion> loadedVersions = new HashMap<>();
    private final Collection<IJavaVersion> versionCache = new LinkedList<>();
    private IJavaVersion selectedVersion;

    public JavaVersionRepository() {
        IJavaVersion version = new CurrentJavaVersion();
        selectedVersion = version;
        loadedVersions.put(null, version);
    }

    public boolean addVersion(IJavaVersion version) {
        if (!version.verify())
            return false;

        File path = version.getJavaPath();

        if (versionCache.contains(version))
            return false;

        loadedVersions.put(path, version);
        versionCache.add(version);
        if (selectedVersion == null)
            selectedVersion = version;

        return true;
    }

    public IJavaVersion getBest64BitVersion() {
        IJavaVersion bestVersion = null;
        for (IJavaVersion version : loadedVersions.values()) {
            if (version.is64Bit()) {
                if (bestVersion == null || bestVersion.getVersionNumber() == null) {
                    bestVersion = version;
                    continue;
                }

                if (version.getVersionNumber() == null)
                    continue;

                if (version.getVersionNumber().compareTo(bestVersion.getVersionNumber()) > 0)
                    bestVersion = version;
            }
        }

        return bestVersion;
    }

    public Collection<IJavaVersion> getVersions() {
        return versionCache;
    }

    public IJavaVersion getSelectedVersion() {
        return selectedVersion;
    }

    public void selectVersion(String version, boolean is64Bit) {
        selectedVersion = getVersion(version, is64Bit);
    }

    public IJavaVersion getVersion(String version, boolean is64Bit) {
        if (version == null || version.isEmpty() || version.equals("default")) {
            return loadedVersions.get(null);
        } else if (version.equals("64bit")) {
            IJavaVersion best64BitVersion = getBest64BitVersion();
            if (best64BitVersion == null)
                best64BitVersion = loadedVersions.get(null);
            return best64BitVersion;
        } else {
            for (IJavaVersion checkVersion : versionCache) {
                if (version.equals(checkVersion.getVersionNumber()) && is64Bit == checkVersion.is64Bit())
                    return checkVersion;
            }

            IJavaVersion specifiedVersion = loadedVersions.get(new File(version));

            if (specifiedVersion == null) {
                specifiedVersion = loadedVersions.get(null);
            }

            return specifiedVersion;
        }
    }

    public String getSelectedPath() {
        if (selectedVersion == null || selectedVersion.getJavaPath() == null)
            return OperatingSystem.getJavaDir();

        return selectedVersion.getJavaPath().getAbsolutePath();
    }
}
