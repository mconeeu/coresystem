/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PartyInventory extends CoreInventory {

    PartyInventory(Player p) {
        super("§8» §5§lMeine Party", p, 54, Option.FILL_EMPTY_SLOTS);

        BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(player, member -> {
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

                    setItem(i, ItemBuilder.createSkullItem(data[0], 1).displayName("§f§l" + data[0]).lore((String[]) lores.toArray()).create(), () -> {
                        new PartyMemberInventory(p, data[0]);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });
                    i++;
                }

                setItem(45, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), () -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new ProfileInventory(p);
                });

                if (isPartyLeader)
                    setItem(49, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§cParty löschen").create(), () -> {
                        p.closeInventory();
                        BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD" ,"party delete");
                    });

                setItem(53, new ItemBuilder(Material.SLIME_BALL, 1, 0).displayName("§4Party verlassen").create(), () -> {
                    p.closeInventory();
                    BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD" ,"party leave");
                });
            } else {
                setItem(22, new ItemBuilder(Material.CAKE, 1, 0).displayName("§5Party erstellen").create(), () -> {
                    p.closeInventory();
                    BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD" ,"party create");
                });

                setItem(45, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), () -> {
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
