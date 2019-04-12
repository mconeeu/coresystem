/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.json;

import com.google.gson.*;
import com.mongodb.MongoClientSettings;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location>, Codec<Location> {

    private static final Codec<Document> documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);

    @Override
    public Location deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("not a JSON object");
        } else {
            final JsonObject obj = (JsonObject) json;
            final JsonElement world = obj.get("world");
            final JsonElement x = obj.get("x");
            final JsonElement y = obj.get("y");
            final JsonElement z = obj.get("z");
            final JsonElement yaw = obj.get("yaw");
            final JsonElement pitch = obj.get("pitch");

            if (world == null || x == null || y == null || z == null || yaw == null || pitch == null) {
                throw new JsonParseException("Malformed location json string!");
            }

            if (!world.isJsonPrimitive() || !((JsonPrimitive) world).isString()) {
                throw new JsonParseException("world is not a string");
            }

            if (!x.isJsonPrimitive() || !((JsonPrimitive) x).isNumber()) {
                throw new JsonParseException("x is not a number");
            }

            if (!y.isJsonPrimitive() || !((JsonPrimitive) y).isNumber()) {
                throw new JsonParseException("y is not a number");
            }

            if (!z.isJsonPrimitive() || !((JsonPrimitive) z).isNumber()) {
                throw new JsonParseException("z is not a number");
            }

            if (!yaw.isJsonPrimitive() || !((JsonPrimitive) yaw).isNumber()) {
                throw new JsonParseException("yaw is not a number");
            }

            if (!pitch.isJsonPrimitive() || !((JsonPrimitive) pitch).isNumber()) {
                throw new JsonParseException("pitch is not a number");
            }

            World bukkitWorld = Bukkit.getWorld(world.getAsString());
            if (bukkitWorld == null) {
                throw new IllegalArgumentException("Unknown/not loaded world");
            }

            return new Location(bukkitWorld, x.getAsDouble(), y.getAsDouble(), z.getAsDouble(), yaw.getAsFloat(), pitch.getAsFloat());
        }
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("world", location.getWorld().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());

        return obj;
    }

    @Override
    public Location decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        final String world = document.getString("world");
        final Double x = document.getDouble("x");
        final Double y = document.getDouble("y");
        final Double z = document.getDouble("z");
        final Double yaw = document.getDouble("yaw");
        final Double pitch = document.getDouble("pitch");

        if (world == null || x == null || y == null || z == null || yaw == null || pitch == null) {
            throw new BSONException("Malformed location BSON string!");
        }

        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            throw new IllegalArgumentException("Unknown/not loaded world");
        }

        return new Location(bukkitWorld, x, y, z, yaw.floatValue(), pitch.floatValue());
    }

    @Override
    public void encode(BsonWriter writer, Location location, EncoderContext encoderContext) {
        Document document = new Document("world", location.getWorld().getName())
                .append("x", location.getX())
                .append("y", location.getY())
                .append("z", location.getZ())
                .append("yaw", location.getYaw())
                .append("pitch", location.getPitch());

        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
