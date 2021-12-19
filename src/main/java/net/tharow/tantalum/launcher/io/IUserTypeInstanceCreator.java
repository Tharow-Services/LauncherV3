package net.tharow.tantalum.launcher.io;

import com.google.gson.*;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.authlib.AuthlibUser;
import net.tharow.tantalum.minecraftcore.microsoft.auth.MicrosoftUser;
import net.tharow.tantalum.minecraftcore.mojang.auth.MojangUser;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class IUserTypeInstanceCreator implements JsonDeserializer<IUserType>, JsonSerializer<IUserType> {

    @Override
    public IUserType deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        System.out.println("User deserialization");
        JsonObject rootObject = json.getAsJsonObject();
        JsonElement userType = rootObject.get("userType");
        String userString = userType == null ? null: userType.getAsString();
        if (userString == null) {
            System.out.println("Deserializing Null user, Uh Wait What? How? Never mind I Don't Even want to know how we got here.");
            return MojangUtils.getGson().fromJson(rootObject, MojangUser.class);
        }
        if (userString.equals(MojangUser.MOJANG_USER_TYPE)) {
            System.out.println("Deserializing Mojang user");
            return MojangUtils.getGson().fromJson(rootObject, MojangUser.class);
        }
        if (userString.equals(AuthlibUser.AUTHLIB_USER_TYPE)) {
            System.out.println("Deserializing Authlib-Injector user");
            return MojangUtils.getGson().fromJson(rootObject, AuthlibUser.class);
        }
        if (userString.equals(MicrosoftUser.MC_MS_USER_TYPE)) {
            System.out.println("Deserializing Microsoft user");
            return MojangUtils.getGson().fromJson(rootObject, MicrosoftUser.class);
        }
        return null;
    }

    @Override
    public JsonElement serialize(IUserType src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof AuthlibUser) {
            JsonElement userElement = MojangUtils.getGson().toJsonTree(src);
            userElement.getAsJsonObject().addProperty("userType", AuthlibUser.AUTHLIB_USER_TYPE);
            return userElement;
        }
        if (src instanceof MojangUser) {
            JsonElement userElement = MojangUtils.getGson().toJsonTree(src);
            userElement.getAsJsonObject().addProperty("userType", MojangUser.MOJANG_USER_TYPE);
            return userElement;
        }
        if (src instanceof MicrosoftUser) {
            JsonElement userElement = MojangUtils.getGson().toJsonTree(src);
            userElement.getAsJsonObject().addProperty("userType", MicrosoftUser.MC_MS_USER_TYPE);
            return userElement;
        }
        return null;
    }
}
