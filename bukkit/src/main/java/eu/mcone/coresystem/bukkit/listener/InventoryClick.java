/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.inventory.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClick implements Listener{

	@EventHandler
	public void on(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();

		if((e.getRawSlot() < e.getInventory().getSize()) && (e.getCurrentItem() != null)) {
			if (e.getCurrentItem() == null || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
				e.setCancelled(false);
			} else if (e.getInventory().getName().equalsIgnoreCase("§8» §3MCONE-Stats")) {
				e.setCancelled(true);

				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(CoreSystem.statsSkypvp.getName())) {
					new StatsCategoryInventory(p, CoreSystem.statsSkypvp);
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
				} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(CoreSystem.statsKnockit.getName())) {
					new StatsCategoryInventory(p, CoreSystem.statsKnockit);
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
				} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(CoreSystem.statsBedwars.getName())) {
					new StatsCategoryInventory(p, CoreSystem.statsBedwars);
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
				} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7§l↩ Zurück zum Stats Menü")) {
					new StatsInventory(p);
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
				} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7§l↩ Zurück zum Profil")) {
					new ProfileInventory(CoreSystem.getCorePlayer(p));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
				}
			} else if (e.getInventory().getName().equalsIgnoreCase("§8» §8Profil von §3§l" + p.getName())) {
				ProfileInventory.click(e, p);
			} else if (e.getInventory().getName().equalsIgnoreCase("§8» §3§lMeine Freunde")) {
				FriendsInventory.click(e, p);
			} else if (e.getInventory().getName().equalsIgnoreCase("§8» §5§lMeine Party")) {
				PartyInventory.click(e, p);
			} else if (e.getInventory().getName().contains("§8| §7Aktionen")) {
				FriendInventory.click(e, p);
			} else if (e.getInventory().getName().contains("§8| §5Aktionen")) {
				PartyMemberInventory.click(e, p);
			}
		}
	}
}
