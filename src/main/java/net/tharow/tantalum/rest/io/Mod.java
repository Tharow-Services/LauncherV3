

package net.tharow.tantalum.rest.io;

@SuppressWarnings({"unused"})
public class Mod extends Resource {
    private String name;
    private String version;

    public Mod() {

    }

    public Mod(String name, String version, String url, String md5) {
        super(url, md5);
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Mod{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", url='" + getUrl() + '\'' +
                ", md5='" + getMd5() + '\'' +
                '}';
    }
}
