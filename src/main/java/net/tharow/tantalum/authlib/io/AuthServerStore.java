package net.tharow.tantalum.authlib.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.authlib.AuthlibAuthenticator;
import net.tharow.tantalum.authlib.AuthlibServer;
import net.tharow.tantalum.launcher.io.IStore;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.utilslib.AnnotationExclusionStrategy;
import net.tharow.tantalum.utilslib.DoNotSerialize;
import net.tharow.tantalum.utilslib.UUIDTypeAdapter;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class AuthServerStore implements IStore {

    private transient File loadedFile;
    //private transient final Class Type;

    private final Map<UUID, AuthlibServer> authlibServerHashMap = new HashMap<>();
    private final List<UUID> byName = new ArrayList<>();

    private static final Gson gson;
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.enableComplexMapKeySerialization();
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC, Modifier.PROTECTED);
        builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
        gson = builder.create();

        // Make sure we're logging everything we want to be logging
    }

    //public TechnicInstalledPackStore(File jsonFile) {
    //    setLoadedFile(jsonFile);
    //}
    private transient final Logger l;

    private AuthServerStore(){
        l = null;
    }

    protected AuthServerStore(File jsonFile, String name){
        this.loadedFile = jsonFile;
        this.l = Logger.getLogger(name);
        this.l.setParent(Utils.getLogger());
        this.l.setLevel(Utils.getLogger().getLevel());
    }
    

    public static @NotNull AuthServerStore load(String name, @NotNull File jsonFile) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(Utils.getLogger());
        logger.setLevel(Utils.getLogger().getLevel());

        if (!jsonFile.exists()) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile + " because it does not exist.");
            return new AuthServerStore(jsonFile, name);
        }

        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            AuthServerStore parsedList = Utils.getGson().fromJson(json, AuthServerStore.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new AuthServerStore(jsonFile, name);
        } catch (JsonSyntaxException | IOException e) {
            logger.log(Level.WARNING, "Unable to load "+name+" from " + jsonFile);
            return new AuthServerStore(jsonFile, name);
        }
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
    }

    public Map<UUID, AuthlibServer> getMap() {
        return authlibServerHashMap;
    }

    
    public List<UUID> getNames() {
        return byName;
    }
    
    public AuthlibServer put(@NotNull AuthlibServer object) {
        AuthlibServer obj = authlibServerHashMap.put(object.get(), object.prepare());
        if (obj == null) {
            byName.add(object.get());
        }
        save();
        return obj;
    }
    
    public AuthlibServer remove(UUID name) {
        AuthlibServer obj = authlibServerHashMap.remove(name);
        if (obj != null) {
            byName.remove(name);
        }
        save();
        return obj;
    }



    @Override
    public void save() {
        {
            String json = gson.toJson(this);

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

}

