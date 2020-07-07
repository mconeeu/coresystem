/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.material.Door;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Getter
public class PlayInUseCodec extends Codec<PacketPlayInUseEntity> {

    private double x;
    private double y;
    private double z;
    private String worldName;
    private boolean action;
    private PlayInUseAction interactWith;

    public PlayInUseCodec() {
        super("INTERACT");
    }

    @Override
    public void decode(Player player, PacketPlayInUseEntity packet) {
        if (packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT)) {
            Location blockLocation = new Location(player.getWorld(), packet.b().a, packet.b().b, packet.b().c);

            if (blockLocation.getBlock().getType().equals(Material.WOOD_BUTTON) || blockLocation.getBlock().getType().equals(Material.STONE_BUTTON)) {
                org.bukkit.block.BlockState blockState = blockLocation.getBlock().getState();
                action = ((Button) blockState.getData()).isPowered();
                interactWith = PlayInUseAction.BUTTON;
            } else if (blockLocation.getBlock().getType().toString().contains("_DOOR")) {
                org.bukkit.block.BlockState blockState = blockLocation.getBlock().getState();
                action = ((Door) blockState.getData()).isOpen();
                interactWith = PlayInUseAction.DOOR;
            } else {
                interactWith = PlayInUseAction.BLOCK;
            }

            worldName = player.getName();
            x = blockLocation.getX();
            y = blockLocation.getY();
            z = blockLocation.getZ();
        }
    }

    @Override
    public List<Packet<?>> encode(Object object) {
        if (object instanceof PlayerNpc) {
            PlayerNpc npc = (PlayerNpc) object;
            Location location = calculateLocation();
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
            IBlockData iblockdata = world.getType(blockposition);

            switch (interactWith) {
                case BUTTON:
                    BlockStateDirection FACING = BlockStateDirection.of("facing");
                    Block block = iblockdata.getBlock();

                    block.interact(((CraftWorld) location.getWorld()).getHandle(), blockposition, iblockdata, null, iblockdata.get(FACING), (float) location.getX(), (float) location.getY(), (float) location.getZ());
                    break;
                case DOOR:
                    BlockStateBoolean OPEN = BlockStateBoolean.of("open");
                    BlockStateEnum<BlockDoor.EnumDoorHalf> HALF = BlockStateEnum.of("half", BlockDoor.EnumDoorHalf.class);

                    BlockPosition blockposition1 = iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
                    IBlockData iblockdata1 = blockposition.equals(blockposition1) ? iblockdata : world.getType(blockposition1);

                    iblockdata = iblockdata1.a(OPEN);
                    world.setTypeAndData(blockposition1, iblockdata, 2);
                    world.b(blockposition1, blockposition);
                    world.a(iblockdata.get(OPEN) ? 1003 : 1006, blockposition, 0);

                    if (action) {
                        location.getWorld().playSound(location, Sound.DOOR_OPEN, 1, 1);
                    } else {
                        location.getWorld().playSound(location, Sound.DOOR_CLOSE, 1, 1);
                    }
                    break;
            }

            npc.sendAnimation(NpcAnimation.SWING_ARM);
        }

        return null;
    }

    public Location calculateLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, 0, 0);
    }

    @Override
    public void onWriteObject(ObjectOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeUTF(worldName);
        out.writeBoolean(action);
        out.writeUTF(interactWith.toString());
    }

    @Override
    public void onReadObject(ObjectInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        worldName = in.readUTF();
        action = in.readBoolean();
        PlayInUseAction.valueOf(in.readUTF());
    }

    @Override
    public Class<PacketPlayInUseEntity> getCodecClass() {
        return PacketPlayInUseEntity.class;
    }
}
