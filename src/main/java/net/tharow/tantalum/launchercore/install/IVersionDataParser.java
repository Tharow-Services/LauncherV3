

package net.tharow.tantalum.launchercore.install;

public interface IVersionDataParser<VersionData> {
    VersionData parseVersionData(String data);
}
