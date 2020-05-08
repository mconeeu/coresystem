package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.labymod.LabyModAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface LabyModBukkitAPI extends LabyModAPI<Player> {

    void updateGameInfo(Player player, Gamemode gamemode, long startTime, long endTime);

    void forceEmote(Player receiver, UUID npcUUID, int emoteId);

    void forceSticker(Player receiver, UUID npcUuid, short stickerId);
}
