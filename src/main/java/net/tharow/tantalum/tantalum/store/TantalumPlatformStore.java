/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2015 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.tantalum.store;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.launcher.io.IStore;
import net.tharow.tantalum.platform.io.PlatformInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.tantalum.io.Platform;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class TantalumPlatformStore implements IStore {
    private static final transient Platform NULL_PLATFORM = new Platform();
    private static final transient Logger l = Logger.getLogger("Platform Store");
    private transient File loadedFile;
    private final Map<UUID, Platform> knownPlatforms = new HashMap<>();
    private final Map<String, UUID> slugDictionary = new HashMap<>();

    public TantalumPlatformStore(File jsonFile, final boolean doRefresh) {
        l.entering(this.getClass(), "Constructor", new Object[]{jsonFile, doRefresh});
        setLoadedFile(jsonFile);
        if(doRefresh){refresh();}
        l.constructor("Platform Store is done being built");
        l.exiting(this.getClass(), "Constructor", this);
    }

    public static @NotNull TantalumPlatformStore load(@NotNull File jsonFile, final boolean doRefresh) {
        l.entering(TantalumPlatformStore.class, "load(@NotNull File jsonFile, final boolean doRefresh)", new Object[]{jsonFile, doRefresh});
        if (!jsonFile.exists()) {
            l.warning("Unable to load installedPacks from " + jsonFile + " because it does not exist.");
            l.exiting(TantalumPlatformStore.class, "load(@NotNull File jsonFile, final boolean doRefresh)", "TantalumPlatform Store");
            return new TantalumPlatformStore(jsonFile, doRefresh);
        }
        l.config("A Json File Was found loading file");
        try {
            String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            TantalumPlatformStore parsedList = Utils.getGson().fromJson(json, TantalumPlatformStore.class);

            if (parsedList != null) {
                parsedList.setLoadedFile(jsonFile);
                return parsedList;
            } else
                return new TantalumPlatformStore(jsonFile, doRefresh);
        } catch (JsonSyntaxException | IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load Platforms from " + jsonFile);
            return new TantalumPlatformStore(jsonFile,doRefresh);
        }
    }

    protected synchronized void refresh(){
        for (Map.Entry<UUID, Platform> entry : knownPlatforms.entrySet()) {
            entry.getValue().


        }
        save();
        l.info("The Tantalum Platform Store has finished refreshing");
    }

    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
        //Utils.logDebug("Refreshing Platform Store");
        //refresh();
        //Refresh Platform Store
    }

    public Platform put(Platform platform) {
        Platform platformInfo = knownPlatforms.put(platform.getHost(), platform);
        if (platformInfo == null) {
            byUUID.add(platform.getHost());
        }
        save();
        return platformInfo;
    }

    public Platform remove(String host) {
        Platform platform = knownPlatforms.remove(host);
        if (platform != null) {
            byUUID.remove(host);
        }
        save();
        return platform;
    }

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

    public Map<String, Platform> getMap() {
        return knownPlatforms;
    }
}
