package net.tharow.tantalum.tantalum.store;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launcher.io.IStore;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.tantalum.io.Platform;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

public class PlatformStore implements IStore {

    private transient File loadedFile;

    private final Map<UUID, Platform> knownPlatforms = new HashMap<>();
    private final List<UUID> byUUID = new ArrayList<>();
    private final Map<String, UUID> slugDictionary = new Hashtable<>();
    private final List<String> bySlug = new ArrayList<>();

    private static Logger l = Logger.getLogger("Platform Store");
    static {
        l.setParent(Utils.getLogger());
        l.setLevel(Utils.getLogger().getLevel());
    }

    protected PlatformStore(File jsonFile){
        this.loadedFile = jsonFile;
    }

    public synchronized void refresh(){
        knownPlatforms.replaceAll((uuid, platform) -> {
            platform.refresh();
            return platform;
        });
    }

    public static @NotNull PlatformStore load(@NotNull File jsonFile) {
        l.setParent(Utils.getLogger());
        l.setLevel(Utils.getLogger().getLevel());

        if (!jsonFile.exists()) {
            l.log(Level.WARNING, "Unable to load Platforms from " + jsonFile + " because it does not exist.");
            return new PlatformStore(jsonFile);
        }

        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            PlatformStore parsedList = Utils.getGson().fromJson(json, PlatformStore.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new PlatformStore(jsonFile);
        } catch (JsonSyntaxException | IOException e) {
            l.log(Level.WARNING, "Unable to load Platforms from " + jsonFile);
            return new PlatformStore(jsonFile);
        }
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
    }

    public Map<UUID, Platform> getPlatforms() {
        return knownPlatforms;
    }

    public Map<String, UUID> getDictionary() {
        return slugDictionary;
    }

    public List<UUID> getByUUID() {
        return byUUID;
    }

    public List<String> getBySlug() {
        return bySlug;
    }

    public String put(String slug, UUID server) {
        if (!byUUID.contains(server)) return slug;
        if (bySlug.contains(slug)) return slug;
        UUID uuid = slugDictionary.put(slug, server);
        if (uuid == null) {
            bySlug.add(slug);
        }
        save();
        return null;
    }

    public UUID remove(String slug) {
        UUID uuid = slugDictionary.remove(slug);
        if (uuid != null) {
            bySlug.remove(slug);
        }
        return uuid;
    }

    public Platform put(Platform object) {
        Platform obj = knownPlatforms.put(object.get(), object);
        if (obj == null) {
            byUUID.add(object.get());
        }
        save();
        return obj;
    }

    public Platform remove(UUID name) {
        Platform obj = knownPlatforms.remove(name);
        if (obj != null) {
            byUUID.remove(name);
        }
        save();
        return obj;
    }

    @Override
    public void save() {
        String json = Utils.getGson().toJson(this);

        File tmpFile = new File(loadedFile.getAbsolutePath() + ".tmp");

        try {
            // UUID we write to a temp file, then we move that file to the intended path
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

