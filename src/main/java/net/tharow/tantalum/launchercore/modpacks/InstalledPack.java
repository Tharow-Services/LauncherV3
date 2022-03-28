

package net.tharow.tantalum.launchercore.modpacks;

public class InstalledPack {
    public static final String RECOMMENDED = "recommended";
    public static final String LATEST = "latest";
    public static final String LAUNCHER_DIR = "launcher\\";
    public static final String MODPACKS_DIR = "%MODPACKS%\\";

    private String name;
    private String build;
    private String directory;
    private String source;

    public InstalledPack(String name, String build, String directory, String source) {
        this();
        this.name = name;
        this.build = build;
        this.directory = directory;
        this.source = source;
    }

    public InstalledPack(String name, String build, String source) {
        this(name, build, MODPACKS_DIR + name, source);
    }

    public InstalledPack() {
        build = RECOMMENDED;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "InstalledPack{" +
                ", name='" + name + '\'' +
                ", build='" + build + '\'' +
                ", directory='" + directory + '\'' +
                '}';
    }
}
