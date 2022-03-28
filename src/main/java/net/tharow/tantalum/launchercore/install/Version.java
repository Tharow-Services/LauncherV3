

package net.tharow.tantalum.launchercore.install;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class Version {
    private String version;
    private boolean legacy;

    public Version() {

    }

    public Version(String version, boolean legacy) {
        this.version = version;
        this.legacy = legacy;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public String getVersion() {
        return version;
    }

    public static Version load(File version) {
        if (!version.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load version from " + version + " because it does not exist.");
            return null;
        }

        try {
            String json = FileUtils.readFileToString(version, StandardCharsets.UTF_8);
            return Utils.getGson().fromJson(json, Version.class);
        } catch (JsonSyntaxException | IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load version from " + version);
            return null;
        }
    }

    public void save(File saveDirectory) {
        File version = new File(saveDirectory, "version");
        String json = Utils.getGson().toJson(this);

        try {
            FileUtils.writeStringToFile(version, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save installed " + version);
        }
    }

    @Override
    public String toString() {
        return version + " " + legacy;
    }
}
