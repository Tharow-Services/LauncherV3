

package net.tharow.tantalum.launchercore.launch.java.source;

import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.launchercore.launch.java.IVersionSource;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.source.os.MacInstalledJavaSource;
import net.tharow.tantalum.launchercore.launch.java.source.os.WinRegistryJavaSource;

/**
 * This IVersionSource is used to collect the known-installed versions of java.  The code is OS-specific,
 * so the logic here is mainly for breaking out to the OS-specific versions
 */
public class InstalledJavaSource implements IVersionSource {
    @Override
    public void enumerateVersions(JavaVersionRepository repository) {
        if (OperatingSystem.getOperatingSystem() == OperatingSystem.WINDOWS)
            (new WinRegistryJavaSource()).enumerateVersions(repository);
        else if (OperatingSystem.getOperatingSystem() == OperatingSystem.OSX)
            (new MacInstalledJavaSource()).enumerateVersions(repository);
    }
}
