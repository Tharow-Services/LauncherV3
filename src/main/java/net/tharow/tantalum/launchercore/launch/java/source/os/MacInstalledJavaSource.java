

package net.tharow.tantalum.launchercore.launch.java.source.os;

import net.tharow.tantalum.launchercore.launch.java.IVersionSource;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.version.FileBasedJavaVersion;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;

public class MacInstalledJavaSource implements IVersionSource {
    @Override
    public void enumerateVersions(JavaVersionRepository repository) {
        repository.addVersion(new FileBasedJavaVersion(new File(getMacJava("1.6"))));
        repository.addVersion(new FileBasedJavaVersion(new File(getMacJava("1.7"))));
        repository.addVersion(new FileBasedJavaVersion(new File(getMacJava("1.8"))));
        repository.addVersion(new FileBasedJavaVersion(new File ("/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java")));
        enumerateJavaHome(repository);
    }

    protected String getMacJava(String versionNumber) {
        String path = Utils.getProcessOutput("/usr/libexec/java_home", "-v", versionNumber);
        return path + File.separator + "bin" + File.separator + "java";
    }

    private void enumerateJavaHome(JavaVersionRepository repository) {
        String output = Utils.getProcessOutput("/usr/libexec/java_home", "-V");

        if (output == null || output.isEmpty())
            return;

        for (String line : output.split("\\r?\\n")) {
            // Output is:
            // Matching Java Virtual Machines (N):
            //     <version> (<arch>) "<vendor>" - "<runtime name>" <path>
            // <path> always starts with /Library/

            int pathIndex = line.indexOf("/Library/");

            if (pathIndex < 0)
                continue;

            String path = line.substring(pathIndex).trim();
            repository.addVersion(new FileBasedJavaVersion(new File(path + File.separator + "bin" + File.separator + "java")));
        }
    }
}
