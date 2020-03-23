package eu.mcone.coresystem.bukkit.npc.nms;

import com.mojang.authlib.GameProfile;
import eu.mcone.coresystem.bukkit.npc.player.net.EmptyNetHandler;
import eu.mcone.coresystem.bukkit.npc.player.net.EmptyNetworkManager;
import eu.mcone.coresystem.bukkit.npc.player.net.EmptySocket;
import net.minecraft.server.v1_8_R3.*;

import java.io.IOException;
import java.net.Socket;

public class EntityHumanNPC extends EntityPlayer {

    private static final double PATH_FINDING_RANGE = 25F;

    public EntityHumanNPC(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerInteractManager) {
        super(minecraftserver, worldserver, gameprofile, playerInteractManager);
        playerInteractManager.setGameMode(WorldSettings.EnumGamemode.SURVIVAL);

        try {
            Socket socket = new EmptySocket();
            NetworkManager conn = new EmptyNetworkManager(EnumProtocolDirection.CLIENTBOUND);

            this.playerConnection = new EmptyNetHandler(
                    minecraftserver,
                    conn,
                    this
            );

            conn.a(this.playerConnection);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AttributeInstance range = getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        if (range == null) {
            range = getAttributeMap().b(GenericAttributes.FOLLOW_RANGE);
        }
        range.setValue(PATH_FINDING_RANGE);

        //Set step height (default (0) breaks step climbing)
        this.S = 1;

        // set skin flag byte
        setSkinFlags((byte) 0xFF);
    }

    public void setSkinFlags(byte flags) {
        // set skin flag byte
        getDataWatcher().watch(10, flags);
    }

}
