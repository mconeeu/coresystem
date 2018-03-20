/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;

public class PartyInventory extends CoreInventory {

    PartyInventory(Player p) {
        super("§8» §5§lMeine Party", p, 54, Option.FILL_EMPTY_SLOTS);

        new PluginMessage(player, member -> {
            if (!member.equals("false")) {
                String[] members = member.split(",");
                boolean isPartyLeader = isPartyLeader(player, members);

                int i = 0;
                for (String m : members) {
                    if (m.equals("") || i > 44) continue;
                    String[] data = m.split(":");

                    List<String> lores = new ArrayList<>();
                    if (data.length>2 && data[2].equals("leader")) lores.add("§e\u2600 Leader");
                    if (isPartyLeader && !data[0].equalsIgnoreCase(player.getName())) lores.addAll(Arrays.asList("", "§8» §f§nRechtsklick§8 | §7§oAktionen"));

                    setItem(i, ItemFactory.createSkullItem("§f§l" + data[0], data[0], 1, lores), () -> {
                        new PartyMemberInventory(p, data[0]);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });
                    i++;
                }

                setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true), () -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new ProfileInventory(p);
                });

                if (isPartyLeader)
                    setItem(49, ItemFactory.createItem(Material.BARRIER, 0, 1, "§cParty löschen", true), () -> {
                        p.closeInventory();
                        new PluginMessage(p, "CMD" ,"party delete");
                    });

                setItem(53, ItemFactory.createItem(Material.SLIME_BALL, 0, 1, "§4Party verlassen", true), () -> {
                    p.closeInventory();
                    new PluginMessage(p, "CMD" ,"party leave");
                });
            } else {
                setItem(22, ItemFactory.createItem(Material.CAKE, 0, 1, "§5Party erstellen", true), () -> {
                    p.closeInventory();
                    new PluginMessage(p, "CMD" ,"party create");
                });

                setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true), () -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new ProfileInventory(p);
                });
            }

            openInventory();
        }, "PARTY", "member");
    }

    private static boolean isPartyLeader(Player p, String[] members) {
        for (String m : members) {
            String data[] = m.split(":");
            if (data[0].equals(p.getName()) && data.length>2 && data[2].equals("leader")) return true;
        }
        return false;
    }

}
