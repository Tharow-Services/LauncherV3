package net.tharow.tantalum.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launchercore.logging.Logger;
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
import java.util.function.Supplier;
import java.util.logging.Level;

public class GenericStoreDictionary<Value, Index, Name> implements IStore {

    private transient File loadedFile;

    private final Map<Index, Value> map = new HashMap<>();
    private final List<Index> mapIndex = new ArrayList<>();
    private final Map<Name, Index> dict = new HashMap<>();
    private final List<Name> dictIndex = new ArrayList<>();

    private final Logger l;

    protected GenericStoreDictionary(File jsonFile, String name){
        this.loadedFile = jsonFile;
        this.l = Logger.getLogger(name);
        this.l.setParent(Utils.getLogger());
        this.l.setLevel(Utils.getLogger().getLevel());
    }
    

    public static <T, F, N> @NotNull GenericStoreDictionary load(String name, @NotNull File jsonFile) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(Utils.getLogger());
        logger.setLevel(Utils.getLogger().getLevel());

        if (!jsonFile.exists()) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile + " because it does not exist.");
            return new GenericStoreDictionary<T, F, N>(jsonFile, name);
        }

        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            GenericStoreDictionary<T, F, N> parsedList = Utils.getGson().fromJson(json, GenericStoreDictionary.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new GenericStoreDictionary<T, F, N>(jsonFile, name);
        } catch (JsonSyntaxException | IOException e) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile);
            return new GenericStoreDictionary<T, F, N>(jsonFile, name);
        }
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
    }
    
    public Map<Index, Value> getMap() {
        return map;
    }
    public List<Index> getMapIndex() { return mapIndex;}

    public Map<Name, Index> getDict() {return dict;}
    public List<Name> getDictIndex() {return dictIndex;}
    
    public Value putDict(Index index, Value value) {
        Value object = map.put(index, value);
        if (object == null) {
            mapIndex.add(index);
        }
        save();
        return object;
    }

    public Index putMap(Name index, Index value){
        return value;
    }
    
    public Value remove(Index name) {
        Value obj = map.remove(name);
        if (obj != null) {
            mapIndex.remove(name);
        }
        save();
        return obj;
    }

    @Override
    public synchronized void save() {
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

