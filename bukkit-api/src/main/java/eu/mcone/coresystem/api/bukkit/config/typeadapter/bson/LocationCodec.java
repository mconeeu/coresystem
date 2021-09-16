/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config.typeadapter.bson;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bson.BSONException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Location;

public class LocationCodec implements Codec<Location> {

    private final CodecRegistry registry;

    public LocationCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Location decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = registry.get(Document.class).decode(reader, decoderContext);
        final String world = document.getString("world");
        final Double x = document.getDouble("x");
        final Double y = document.getDouble("y");
        final Double z = document.getDouble("z");
        final Double yaw = document.getDouble("yaw");
        final Double pitch = document.getDouble("pitch");

        if (world == null || x == null || y == null || z == null || yaw == null || pitch == null) {
            throw new BSONException("Malformed location BSON string!");
        }

        CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(world);
        if (w == null) {
            throw new IllegalArgumentException("Unknown/not loaded world");
        }

        if (!w.isLoaded()) {
            w.load();
        }

        return new Location(w.bukkit(), x, y, z, yaw.floatValue(), pitch.floatValue());
    }

    @Override
    public void encode(BsonWriter writer, Location location, EncoderContext encoderContext) {
        Document document = new Document("world", location.getWorld().getName())
                .append("x", location.getX())
                .append("y", location.getY())
                .append("z", location.getZ())
                .append("yaw", location.getYaw())
                .append("pitch", location.getPitch());

        registry.get(Document.class).encode(writer, document, encoderContext);
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
