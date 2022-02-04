/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

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