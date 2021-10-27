package net.tharow.tantalum.launcher.io;

import com.google.gson.*;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.launchercore.auth.TantalumUser;

import java.lang.reflect.Type;

public class IUserTypeInstanceCreator implements JsonDeserializer<IUserType>, JsonSerializer<IUserType> {
    // Test builds previously used this value for the type saved in the user.json for MS accounts
    private static final String BETA_MS_USER_TYPE = "microsoft";

    @Override
    public IUserType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        System.out.println("User deserialization");
        JsonObject rootObject = json.getAsJsonObject();
        JsonElement userType = rootObject.get("userType");
        String userString = userType == null ? null: userType.getAsString();
        if (userString == null || userString.equals(TantalumUser.MOJANG_USER_TYPE)) {
            System.out.println("Deserializing mojang user");
            return MojangUtils.getGson().fromJson(rootObject, TantalumUser.class);
        }
        return null;
    }

    @Override
    public JsonElement serialize(IUserType src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof TantalumUser) {
            JsonElement userElement = MojangUtils.getGson().toJsonTree(src);
            userElement.getAsJsonObject().addProperty("userType", TantalumUser.MOJANG_USER_TYPE);
            return userElement;        }
        return null;
    }
}
