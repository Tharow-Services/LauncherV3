

package net.tharow.tantalum.utilslib;

import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UUIDTypeAdapter implements JsonDeserializer<UUID>, JsonSerializer<UUID> {

    public UUIDTypeAdapter() {
    }

    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The UUID should be a string value");
        }

        UUID uuid = deserializeToUUID(json);

        if (typeOfT == UUID.class) {
            return uuid;
        }
        throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
    }

    private @NotNull UUID deserializeToUUID(JsonElement json) {
        try{
            return UUID.fromString(json.getAsString());
        } catch (Exception e){
            throw new JsonParseException("Invalid UUID: "+json.getAsString(), e);
        }

    }

    public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(serializeToString(src));
    }

    @Contract(pure = true)
    private @NotNull String serializeToString(@NotNull UUID uuid) {
        return uuid.toString();
    }
}