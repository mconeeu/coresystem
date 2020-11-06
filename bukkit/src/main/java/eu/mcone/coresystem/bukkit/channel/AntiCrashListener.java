package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.util.PacketListener;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AntiCrashListener implements PacketListener {

    private enum CheckItem {
        FIREWORK(ItemFireworks.class, Material.FIREWORK),
        WRITTEN_BOOK(ItemWrittenBook.class, Material.WRITTEN_BOOK),
        BOOK_AND_QUILL(ItemBookAndQuill.class, Material.BOOK_AND_QUILL);

        private final Class<? extends Item> item;
        private final Material material;

        CheckItem(Class<? extends Item> item, Material material) {
            this.item = item;
            this.material = material;
        }
    }

    @Override
    public void onPacketIn(Player p, Packet<?> packet) {
        /*if (packet instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace packetPlayInBlockPlace = (PacketPlayInBlockPlace) packet;

            if (packetPlayInBlockPlace.getItemStack().getItem() != null) {
                for (CheckItem item : CheckItem.values()) {
                    if (packetPlayInBlockPlace.getItemStack().getItem().getClass().isAssignableFrom(item.item)) {
                        if (!p.getInventory().contains(item.material)) {
                            if (channel.isOpen()) {
                                Bukkit.getConsoleSender().sendMessage("§eAntiCrash §8︳§e" + sender.getName() + " §8| §eCANT GET THE FIREWORK");
                            }
                            ch.close();

                            return;
                        }
                    }
                }

                if (packetPlayInBlockPlace.getItemStack().getTag().getList("pages", 8).size() > 100) {
                    if (channel.isOpen()) {
                        Bukkit.getConsoleSender().sendMessage("§eAntiCrash §8︳§e" + sender.getName() + " §8| §eTOO MUCH PAGES");
                    }
                    ch.close();
                    return true;
                }
                int i1 = 0;
                for (int i = 0; i < packetPlayInBlockPlace.getItemStack().getTag().getList("pages", 8).size(); i++) {
                    i1 += packetPlayInBlockPlace.getItemStack().getTag().getList("pages", 8).getString(i).length();
                    if (i1 > 1000) {
                        if (ch.isOpen()) {
                            Bukkit.getConsoleSender().sendMessage("§eAntiCrash §8︳§e" + sender.getName() + " §8| §eTOO LONG STRING");
                        }
                        ch.close();
                        return true;
                    }
                }
            }
        } else if (packet instanceof PacketPlayInSetCreativeSlot) {
            if (p.getInventory().firstEmpty() == -1 && p.getGameMode().equals(GameMode.CREATIVE)) {
                CoreSystem.getInstance().getMessenger().sendError(p, "Dein Inventar ist voll. Lösche zuerst erst ein Item aus deinem Inventar!");
            }
        }*/
    }

    @Override
    public void onPacketOut(Player p, Packet<?> packet) {

    }

}
