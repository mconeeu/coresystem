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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Door;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayInUseBlockCodec extends Codec<PlayerInteractEvent, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private double x;
    private double y;
    private double z;
    private boolean action;
    private PlayInUseAction interactWith;

    public PlayInUseBlockCodec() {
        super((short) 2, (short) 2);
    }

    @Override
    public Object[] decode(Player player, PlayerInteractEvent interactEvent) {
        if (interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            org.bukkit.block.Block block = interactEvent.getClickedBlock();
            if (block.getType().equals(Material.WOOD_BUTTON) || block.getType().equals(Material.STONE_BUTTON)) {
                org.bukkit.block.BlockState blockState = block.getState();
                action = ((Button) blockState.getData()).isPowered();
                interactWith = PlayInUseAction.BUTTON;
            } else if (block.getType().toString().contains("_DOOR")) {
                org.bukkit.block.BlockState blockState = block.getState();
                action = ((Door) blockState.getData()).isOpen();
                interactWith = PlayInUseAction.DOOR;
            } else {
                interactWith = PlayInUseAction.BLOCK;
            }

            x = block.getX();
            y = block.getY();
            z = block.getZ();

            return new Object[]{interactEvent.getPlayer()};
        } else if (interactEvent.getAction().equals(Action.LEFT_CLICK_BLOCK) || interactEvent.getAction().equals(Action.LEFT_CLICK_AIR)) {
            interactWith = PlayInUseAction.AIR;
            return new Object[]{interactEvent.getPlayer()};
        }

        return null;
    }

    @Override
    public void encode(PlayerNpc npc) {
        Location location = calculateLocation(npc);
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        IBlockData iblockdata = world.getType(blockposition);

        switch (interactWith) {
            case BUTTON:
                BlockStateDirection FACING = BlockStateDirection.of("facing");
                Block block = iblockdata.getBlock();

                block.interact(((CraftWorld) location.getWorld()).getHandle(), blockposition, iblockdata, null, iblockdata.get(FACING), (float) location.getX(), (float) location.getY(), (float) location.getZ());
                npc.sendAnimation(NpcAnimation.SWING_ARM);
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

                npc.sendAnimation(NpcAnimation.SWING_ARM);
                break;
            case AIR:
                npc.sendAnimation(NpcAnimation.SWING_ARM);
                break;
        }
    }

    public Location calculateLocation(PlayerNpc npc) {
        return new Location(npc.getLocation().getWorld(), x, y, z, 0, 0);
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeBoolean(action);
        out.writeByte(interactWith.getId());
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        action = in.readBoolean();
        interactWith = PlayInUseAction.getByID(in.readByte());
    }

    @Override
    public String toString() {
        return "PlayInUseCodec{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", action=" + action +
                ", interactWith=" + interactWith +
                '}';
    }
}
