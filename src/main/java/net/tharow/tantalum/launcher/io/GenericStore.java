package net.tharow.tantalum.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class GenericStore<Value extends Supplier<Index>, Index> implements IStore {

    private transient File loadedFile;
    //private transient final Class Type;
    
    private final Map<Index, Value> Objects = new HashMap<>();
    private final List<Index> byName = new ArrayList<>();

    //public TechnicInstalledPackStore(File jsonFile) {
    //    setLoadedFile(jsonFile);
    //}
    private final Logger l;
    
    protected GenericStore(File jsonFile, String name){
        this.loadedFile = jsonFile;
        this.l = Logger.getLogger(name);
        this.l.setParent(Utils.getLogger());
        this.l.setLevel(Utils.getLogger().getLevel());
    }
    

    public static <T extends Supplier<F>, F> @NotNull GenericStore load(String name, @NotNull File jsonFile) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(Utils.getLogger());
        logger.setLevel(Utils.getLogger().getLevel());

        if (!jsonFile.exists()) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile + " because it does not exist.");
            return new GenericStore<T, F>(jsonFile, name);
        }

        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            GenericStore<T, F> parsedList = Utils.getGson().fromJson(json, GenericStore.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new GenericStore<T, F>(jsonFile, name);
        } catch (JsonSyntaxException | IOException e) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile);
            return new GenericStore<T, F>(jsonFile, name);
        }
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
    }
    
    public Map<Index, Value> getMap() {
        return Objects;
    }

    
    public List<Index> getNames() {
        return byName;
    }
    
    public Value put(Value object) {
        Value obj = Objects.put(object.get(), object);
        if (obj == null) {
            byName.add(object.get());
        }
        save();
        return obj;
    }
    
    public Value remove(Index name) {
        Value obj = Objects.remove(name);
        if (obj != null) {
            byName.remove(name);
        }
        save();
        return obj;
    }

    @Override
    public void save() {
        String json = Utils.getGson().toJson(this);

        File tmpFile = new File(loadedFile.getAbsolutePath() + ".tmp");

        try {
            // Index we write to a temp file, then we move that file to the intended path
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

