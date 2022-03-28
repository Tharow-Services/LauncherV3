

package net.tharow.tantalum.launchercore.launch.java.version;

import net.tharow.tantalum.utilslib.JavaUtils;
import net.tharow.tantalum.launchercore.launch.java.IJavaVersion;

import java.io.File;

/**
 * An IJavaVersion for the version of java that is currently running this code.
 */
public class CurrentJavaVersion implements IJavaVersion {

    public CurrentJavaVersion() {}


    @Override
    public String getVersionNumber() {
        return System.getProperty("java.version");
    }

    @Override
    public File getJavaPath() {
        return null;
    }

    @Override
    public boolean is64Bit() {
        return JavaUtils.is64Bit();
    }

    @Override
    public boolean isOpenJDK() { return System.getProperty("java.runtime.name").contains("OpenJDK"); }

    @Override
    public boolean verify() {
        return true;
    }
}
