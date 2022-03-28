

package net.tharow.tantalum.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.tharow.tantalum.authlib.AuthlibUser;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.auth.IUserStore;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.microsoft.auth.MicrosoftUser;
import net.tharow.tantalum.minecraftcore.mojang.auth.MojangUser;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TantalumUserStore implements IUserStore, IStore {
    private final String clientToken = UUID.randomUUID().toString();
    private final Map<String, IUserType> savedUsers = new HashMap<>();
    private String lastUser;
    private transient File usersFile;

    public TantalumUserStore() {
    }

    public TantalumUserStore(File userFile) {
        this.usersFile = userFile;
    }

    public static @NotNull TantalumUserStore load(@NotNull File userFile) {
        if (!userFile.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile + " because it does not exist.");
            return new TantalumUserStore(userFile);
        }

        try {
            String json = FileUtils.readFileToString(userFile, StandardCharsets.UTF_8);
            TantalumUserStore newModel = MojangUtils.getGson().fromJson(json, TantalumUserStore.class);

            if (newModel != null) {
                newModel.setUserFile(userFile);
                return newModel;
            }
        } catch (JsonSyntaxException | IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile);
        }

        return new TantalumUserStore(userFile);
    }

    public void setUserFile(File userFile) {
        this.usersFile = userFile;
    }

    public void save() {
        String json = MojangUtils.getGson().toJson(this);

        try {
            FileUtils.writeStringToFile(usersFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save users " + usersFile);
        }
    }

    public void addUser(@NotNull IUserType user) {
        if (savedUsers.containsKey(user.getUsername())) {
            IUserType oldUser = savedUsers.get(user.getUsername());
            if (oldUser instanceof MojangUser && user instanceof MojangUser) {
                ((MojangUser) user).mergeUserProperties((MojangUser) oldUser);
            }
            if (oldUser instanceof AuthlibUser && user instanceof AuthlibUser) {
                ((AuthlibUser) user).mergeUserProperties((AuthlibUser) oldUser);
            }
        }
        savedUsers.put(user.getUsername(), user);
        save();
    }

    public void removeUser(String username) {
        savedUsers.remove(username);
        save();
    }

    public IUserType getUser(String accountName) {
        return savedUsers.get(accountName);
    }

    public String getClientToken() {
        return clientToken;
    }

    public Collection<String> getMojangUsers() {
        return getUsersOf(MojangUser.MOJANG_USER_TYPE).keySet();
    }

    public Collection<IUserType> getSavedMojangUsers() {
        return getUsersOf(MojangUser.MOJANG_USER_TYPE).values();
    }
    public Collection<String> getMicrosoftUsers() {
        return getUsersOf(MicrosoftUser.MC_MS_USER_TYPE).keySet();
    }
    public Collection<IUserType> getSavedMicrosoftUsers() {
        return getUsersOf(MicrosoftUser.MC_MS_USER_TYPE).values();
    }

    protected Map<String, IUserType> getUsersOf(String userType) {
        return savedUsers
                .entrySet()
                .stream()
                .filter((entry) -> !entry.getValue().getMCUserType().equals(userType) )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<String> getUsers() {
        if (!TantalumConstants.isUnlocked()) {
            return getMojangUsers();
        }
        return savedUsers.keySet();
    }

    public Collection<IUserType> getSavedUsers() {
        if (!TantalumConstants.isUnlocked()) {
            return getSavedMojangUsers();
        }
        return savedUsers.values();
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
        save();
    }

    public String getLastUser() {
        return lastUser;
    }
}
