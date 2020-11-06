/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerVersionCheckListener implements Listener {

    @EventHandler
    public void on(PreLoginEvent e) {
        int version = e.getConnection().getVersion();

        if (version < 47) {
            e.setCancelled(true);
            e.setCancelReason(TextComponent.fromLegacyText(
                    "§f§lMC ONE §3Minecraftnetzwerk"
                            + "\n§4§oDu verwendest eine zu alte Minecraft-Version!"
                            + "\n§r"
                            + "\n§fMC ONE §7ist momentan nur über die Version §f1.8.X§7 erreichbar, "
                            + "\n§7um die bekannteste Minecraftversion mit dem beliebtesten PvP-System"
                            + "\n§7zu unterstützen."
            ));
        } else if (version > 47) {
            //e.setCancelled(true);
            e.setCancelReason(TextComponent.fromLegacyText(
                    "§f§lMC ONE §3Minecraftnetzwerk"
                            + "\n§4§oDu verwendest eine zu alte Minecraft-Version!"
                            + "\n§r"
                            + "\n§fMC ONE §7ist momentan nur über die Version §f1.8.X§7 erreichbar, "
                            + "\n§7um die bekannteste Minecraftversion mit dem beliebtesten PvP-System"
                            + "\n§7zu unterstützen."
            ));
        }
    }

}
