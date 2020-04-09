package eu.mcone.coresystem.api.bukkit.util;

import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void playSound(String nmsSound, Location location) {
        playSound(nmsSound, location, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public static void playSound(String nmsSound, Location location, Player[] players) {
        sendNearBy(nmsSound, location, players);
    }

    private static void sendNearBy(String nmsSound, Location location, Player[] players) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        for (Player player : players) {
            double d4 = x - player.getLocation().getX();
            double d5 = y - player.getLocation().getY();
            double d6 = z - player.getLocation().getZ();
            if (d4 * d4 + d5 * d5 + d6 * d6 < 15 * 15) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(nmsSound, x, y, z, 1, 1));
            }
        }
    }
}
