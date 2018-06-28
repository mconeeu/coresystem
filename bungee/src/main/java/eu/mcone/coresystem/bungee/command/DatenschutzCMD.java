/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class DatenschutzCMD extends Command {

    public DatenschutzCMD() {
        super("datenschutz", null, "datenschutzerklärung", "dataprotection", "agb", "agbs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            BungeeCorePlayer p = CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender);

            if (args.length == 1 && (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("akzeptieren"))) {
                if (p.getSettings().isAcceptedAgbs()) {
                    CoreSystem.getInstance().getMessager().send(sender, "§4Du hast die Datenschutzerklärung bereits akzeptiert!");
                } else {
                    PlayerSettings settings = p.getSettings();
                    settings.setAcceptedAgbs(true);

                    p.updateSettings(settings);
                    CoreSystem.getInstance().getMessager().send(sender, "§2Du hast die Datenschutzerklärung erfolgreich akzeptiert!");
                }
            } else {
                ComponentBuilder cb = new ComponentBuilder("§7Du musst unsere Datenschutzerklärung akzeptieren, um auf MC ONE spielen zu können!\n")
                        .append("§f[DATENSCHUTZERKLÄRUNG ÖFFNEN]")
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/datenschutz.php"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oBrowser öffnen").create()));

                if (!p.getSettings().isAcceptedAgbs())
                    cb.append(" ")
                            .append("§a[DATENSCHUTZERKLÄRUNG AKZEPTIEREN]")
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/datenschutz accept"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oKlicke zum akzeptieren").create()));

                p.bungee().sendMessage(cb.create());
            }
        }
    }

}
