/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.templates.EntityLocationTContainer;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityMovePacketContainer extends EntityLocationTContainer {

    public EntityMovePacketContainer(final Location location) {
        super(PacketTyp.ENTITY, EntityAction.MOVE, location);
    }

    public EntityMovePacketContainer(double x, double y, double z, float yaw, float pitch, String world) {
        super(PacketTyp.ENTITY, EntityAction.MOVE, x, y, z, yaw, pitch, world);
    }
}
