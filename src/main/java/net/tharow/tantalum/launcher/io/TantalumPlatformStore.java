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

package net.tharow.tantalum.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.platform.io.PlatformInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TantalumPlatformStore {
    private static final transient Platform NULL_PLATFORM = new Platform();
    private static final transient Logger l = Utils.getLogger();
    private transient File loadedFile;

    private final Map<String, Platform> knownPlatforms = new HashMap<>();
    private final List<String> byHost = new ArrayList<>();
    private final Map<String, String> slugDictionary = new HashMap<>();

    public TantalumPlatformStore(File jsonFile, final boolean doRefresh) {
        setLoadedFile(jsonFile);
        if(doRefresh){refresh();}
    }

    public static @NotNull TantalumPlatformStore load(@NotNull File jsonFile, final boolean doRefresh) {
        if (!jsonFile.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load installedPacks from " + jsonFile + " because it does not exist.");
            return new TantalumPlatformStore(jsonFile, doRefresh);
        }

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

    public synchronized void refresh(){
        byHost.forEach(this::refresh);
        save();
        l.info("The Tantalum Platform Store has finished refreshing");
    }

    protected void refresh(String host){
        Platform oldPlatform = knownPlatforms.get(host);
        String platform = oldPlatform.getUrl();
        Platform newPlatform = getPlatform(platform);
        if(newPlatform.equals(NULL_PLATFORM)){
            remove(oldPlatform.getHost());
            return;
        }
        if(!oldPlatform.equals(newPlatform)){
            remove(oldPlatform.getHost());
            put(newPlatform);
        }
    }

    public String getHostUrl(String host){
        return knownPlatforms.get(host).getUrl();
    }

    public Platform getHostPlatform(String host){
        return knownPlatforms.get(host);
    }


    public void addPlatforms(List<String> list){
        list.forEach(this::addPlatforms);
    }
    private void addPlatforms(String platform){
        Platform newPlatform = getPlatform(platform);
        if(newPlatform.equals(NULL_PLATFORM)){
            Utils.logDebug("Platform Could not be added");
        } else {put(newPlatform);}
        save();
    }


    public Map<String, String> getPlatform() {
        Map<String, String> temp = new HashMap<>();
        for (Map.Entry<String, Platform> entry : knownPlatforms.entrySet()) {
            //String key = entry.getKey();
            Platform value = entry.getValue();
            temp.put(value.getName(),value.getUrl());
        }
        return temp;
    }
    //private void getPlatform(String ignored, Platform platform){
    //    tempMap.put(platform.getName(),platform.getUrl());
    //}
    protected Platform getPlatform(String platform){
        Utils.logDebug("Trying to Connect Platform: " + platform);
        Platform newPlatform = new Platform();
        try{
            try{
                newPlatform = new Platform(RestObject.getRestObject(PlatformInfo.class, platform),platform);
            } catch (RestfulAPIException ignored){
                Utils.logDebug("Failed Trying Again With Ping");
                newPlatform = new Platform(RestObject.getRestObject(PlatformInfo.class, platform + "ping"),platform);
            }
        } catch (RestfulAPIException ignored) {
            Utils.logDebug("Platform Could not be Contacted");
        }

        return newPlatform;
    }


    protected void setLoadedFile(File loadedFile) {
        this.loadedFile = loadedFile;
        //Utils.logDebug("Refreshing Platform Store");
        //refresh();
        //Refresh Platform Store
    }

    public String removeSlug(String slug){
        save();
        return slugDictionary.remove(slug);
    }

    public String putSlug(String slug, String host){
        String r = slugDictionary.put(slug, host);
        save();
        return r;
    }

    public List<String> getHosts(){
        return byHost;
    }

    public String getSlugHost(String slug){
        return slugDictionary.getOrDefault(slug,null);
    }

    public String getSlugUrl(String slug){
        String host = slugDictionary.getOrDefault(slug,null);
        return getHostUrl(host);
    }

    public Platform put(Platform platform) {
        Platform platformInfo = knownPlatforms.put(platform.getHost(), platform);
        if (platformInfo == null) {
            byHost.add(platform.getHost());
        }
        save();
        return platformInfo;
    }

    public Platform remove(String host) {
        Platform platform = knownPlatforms.remove(host);
        if (platform != null) {
            byHost.remove(host);
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
