

package net.tharow.tantalum.launchercore.launch.java;

/**
 * This interface provides an API for adding IJavaVersion objects to a JavaVersionRepository
 */
public interface IVersionSource {
    void enumerateVersions(JavaVersionRepository repository);
}
