

package net.tharow.tantalum.launchercore.launch.java;

import java.io.File;

/**
 * This interface represents a single version of java that can be used to launch a java-based game.
 */
public interface IJavaVersion {
    String getVersionNumber();
    File getJavaPath();
    boolean is64Bit();
    boolean isOpenJDK();
    boolean verify();
}
