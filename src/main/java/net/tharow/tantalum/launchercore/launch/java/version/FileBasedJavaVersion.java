

package net.tharow.tantalum.launchercore.launch.java.version;

import net.tharow.tantalum.launchercore.launch.java.IJavaVersion;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An IJavaVersion based on an externally-selected java executable.
 */
public class FileBasedJavaVersion implements IJavaVersion {
    private transient boolean haveQueriedVersion = false;
    private transient String versionNumber;
    private transient boolean is64Bit;
    private transient boolean isOpenJDK;
    private transient File javaPath;
    private String filePath;

    public FileBasedJavaVersion() {}
    public FileBasedJavaVersion(File javaPath) {
        this.javaPath = javaPath;
        this.filePath = javaPath.getAbsolutePath();
    }

    @Override
    public String getVersionNumber() {
        if (!haveQueriedVersion) {
            verify();
        }

        return versionNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof FileBasedJavaVersion))
            return false;
        return (is64Bit() == ((FileBasedJavaVersion) obj).is64Bit() && getVersionNumber().equals(((FileBasedJavaVersion) obj).getVersionNumber()));
    }

    @Override
    public int hashCode() {
        int hash = getVersionNumber().hashCode();
        hash = (hash * 31) + (is64Bit()?1:0);
        return hash;
    }

    public boolean is64Bit() {
        if (!haveQueriedVersion) {
            verify();
        }

        return is64Bit;
    }

    public boolean isOpenJDK() {
        if(!haveQueriedVersion) {
            verify();
        }

        return isOpenJDK;
    }

    public File getJavaPath() {
        if (javaPath == null && filePath != null && !filePath.isEmpty())
            javaPath = new File(filePath);
        return javaPath;
    }

    /**
     *
     * @return True if the javaPath points to a valid version of java that can be run, false otherwise
     */
    public boolean verify() {
        if (getJavaPath() == null || !getJavaPath().exists())
            return false;

        if (!haveQueriedVersion) {
            haveQueriedVersion = true;
            versionNumber = getVersionNumberFromJava();
        }

        return (versionNumber != null);
    }

    /**
     * Obtain the version number of the java executable in javaPath, by querying with java -version and mangling
     * the output
     *
     * @return The version number of the java executable in the javaPath field, or null if there was a problem with
     * the executable.
     */
    protected String getVersionNumberFromJava() {
        String versionJavaExec = filePath;

        if (versionJavaExec.endsWith("javaw.exe")) {
            //On windows operating systems, we ask people to find javaw.exe, which we use to actually run anything
            //however, javaw -version isn't a thing, only java -version is
            //so we have to change the path to point at java.exe
            versionJavaExec = versionJavaExec.substring(0, versionJavaExec.length() - 5) + ".exe";
        }
        String data = Utils.getProcessOutput(versionJavaExec, "-version");

        if (data == null)
            return null;

        is64Bit = data.contains("64-Bit");

        Pattern versionPattern = Pattern.compile("(java|openjdk) version \"(.+?)\"");
        Matcher versionMatcher = versionPattern.matcher(data);

        if (!versionMatcher.lookingAt())
            return null;

        isOpenJDK = versionMatcher.group(1).equals("openjdk");

        return versionMatcher.group(2);
    }
}
