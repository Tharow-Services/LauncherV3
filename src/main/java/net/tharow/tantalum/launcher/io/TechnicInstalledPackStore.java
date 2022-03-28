

package net.tharow.tantalum.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TechnicInstalledPackStore implements IInstalledPackRepository, IStore {

    private transient File loadedFile;

    private final Map<String, InstalledPack> installedPacks = new HashMap<>();
    private final List<String> byIndex = new ArrayList<>();
    private String selected = null;

    public TechnicInstalledPackStore(File jsonFile) {
        setLoadedFile(jsonFile);
    }

    public static @NotNull TechnicInstalledPackStore load(@NotNull File jsonFile) {
        if (!jsonFile.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load installedPacks from " + jsonFile + " because it does not exist.");
            return new TechnicInstalledPackStore(jsonFile);
        }

        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            TechnicInstalledPackStore parsedList = Utils.getGson().fromJson(json, TechnicInstalledPackStore.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new TechnicInstalledPackStore(jsonFile);
        } catch (JsonSyntaxException | IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load installedPacks from " + jsonFile);
            return new TechnicInstalledPackStore(jsonFile);
        }
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;

        //HACK: "And that's why.... you don't put view data in the model."
        /////////// - J. Walter Weatherman, Software Developer
        installedPacks.remove("addpack");

        byIndex.remove("addpack");
    }

    @Override
    public Map<String, InstalledPack> getInstalledPacks() {
        return installedPacks;
    }

    @Override
    public List<String> getPackNames() {
        return byIndex;
    }

    @Override
    public String getSelectedSlug() {
        return selected;
    }

    @Override
    public void setSelectedSlug(String slug) {
        selected = slug;
    }

    @Override
    public InstalledPack put(InstalledPack installedPack) {
        InstalledPack pack = installedPacks.put(installedPack.getName(), installedPack);
        if (pack == null) {
            byIndex.add(installedPack.getName());
        }
        save();
        return pack;
    }

    @Override
    public InstalledPack remove(String name) {
        InstalledPack pack = installedPacks.remove(name);
        if (pack != null) {
            byIndex.remove(name);
        }
        save();
        return pack;
    }

    @Override
    public void save() {
        String json = Utils.getGson().toJson(this);

        File tmpFile = new File(loadedFile.getAbsolutePath() + ".tmp");

        try {
            // First we write to a temp file, then we move that file to the intended path
            // This way, we won't end up with an empty file if we fail to write to it
            FileUtils.writeStringToFile(tmpFile, json, StandardCharsets.UTF_8);
            if (loadedFile.exists() && !loadedFile.delete())
                throw new IOException("Failed to delete");
            FileUtils.moveFile(tmpFile, loadedFile);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save settings " + loadedFile, e);
        }
    }
}
