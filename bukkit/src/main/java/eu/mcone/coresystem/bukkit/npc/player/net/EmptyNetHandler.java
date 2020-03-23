package eu.mcone.coresystem.bukkit.npc.player.net;

import net.minecraft.server.v1_8_R3.*;

public class EmptyNetHandler extends PlayerConnection {

    public EmptyNetHandler(MinecraftServer minecraftServer, NetworkManager networkManager, EntityPlayer entityPlayer) {
        super(minecraftServer, networkManager, entityPlayer);
    }

    @Override
    public void sendPacket(Packet packet) {
    }

}
