

package net.tharow.tantalum.launchercore.launch.java.source;

import com.google.common.base.Charsets;
import net.tharow.tantalum.launchercore.launch.java.IVersionSource;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.version.FileBasedJavaVersion;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads versions from an external file
 */
public class FileJavaSource implements IVersionSource {
    private transient File loadedFile;
    private final List<FileBasedJavaVersion> versions = new ArrayList<>();

    protected FileJavaSource(File loadFile) {
        this.loadedFile = loadFile;
    }

    protected void setLoadedFile(File loadedFile) { this.loadedFile = loadedFile; }

    @Override
    public void enumerateVersions(JavaVersionRepository repository) {
        // Add all valid Java runtimes to the repository, and remove any invalid ones
        // If any were removed, then we save the cleaned up list
        if (versions.removeIf(version -> !repository.addVersion(version))) {
            save();
        }
    }

    public void addVersion(FileBasedJavaVersion version) {
        versions.add(version);
        save();
    }

    public static FileJavaSource load(File file) {

        if (file == null || !file.exists())
            return new FileJavaSource(file);

        try {
            String sourceText = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            FileJavaSource source = Utils.getGson().fromJson(sourceText, FileJavaSource.class);
            source.setLoadedFile(file);
            return source;
        } catch (IOException ex) {
            ex.printStackTrace();
            return new FileJavaSource(file);
        }
    }

    public void save() {
        String data = Utils.getGson().toJson(this);

        try {
            FileUtils.write(loadedFile, data, Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
