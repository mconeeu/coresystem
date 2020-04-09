package eu.mcone.coresystem.bukkit.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.data.PigNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PigNpc;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.nms.EntityPigNPC;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PigCoreNPC extends CoreNPC<EntityPigNPC, PigNpcData> implements PigNpc {

    public PigCoreNPC(NpcData data, ListMode listMode, Player[] players) {
        super(PigNpcData.class, data, listMode, players);
    }

    @Override
    protected void onCreate() {
        this.entity = new EntityPigNPC(((CraftWorld) location.getWorld()).getHandle());
        this.entity.setCustomName(data.getDisplayname());
        this.entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity);

        Material beneath = location.getBlock().getRelative(BlockFace.DOWN).getType();
        if (beneath.isBlock()) {
            this.entity.onGround = true;
        }
    }

    @Override
    protected void onUpdate(PigNpcData entityData) {

    }

    @Override
    protected void _spawn(Player player) {
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving();
        ReflectionManager.setValue(spawnPacket, "a", entity.getId());
        ReflectionManager.setValue(spawnPacket, "b", EntityType.PIG.getTypeId());
        ReflectionManager.setValue(spawnPacket, "c", MathHelper.floor(location.getX() * 32.0D));
        ReflectionManager.setValue(spawnPacket, "d", MathHelper.floor(location.getY() * 32.0D));
        ReflectionManager.setValue(spawnPacket, "e", MathHelper.floor(location.getZ() * 32.0D));
        ReflectionManager.setValue(spawnPacket, "i", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionManager.setValue(spawnPacket, "j", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        ReflectionManager.setValue(spawnPacket, "l", entity.getDataWatcher());
        
        sendPackets(player, spawnPacket);
        //entity.follow(player);
    }

    @Override
    protected void _despawn(Player player) {
        sendPackets(
                player,
                new PacketPlayOutEntityDestroy(entity.getId())
        );
    }

}
