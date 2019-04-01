/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.teamspeak.TeamspeakVerifier;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TsCMD extends Command {
    public TsCMD() {
        super("ts", null, "teamspeak");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

            if (args.length == 0) {
                p.sendMessage(
                        new ComponentBuilder("")
                                .append(TextComponent.fromLegacyText("§8§m----------------§r§8| §3Teamspeak §8§m|----------------§r"))
                                .append(TextComponent.fromLegacyText(
                                        cp.isTeamspeakIdLinked() ?
                                                "\n§7Du hast diese TeamSpeak ID verlinkt: §f" + cp.getTeamspeakUid() + "\n§7Benutze §3/ts unlink§7 zum entfernen" :
                                                "\n§7§oDu hast keine TeamSpeak Identität verlinkt!\n§2Benutze §a/ts link§2 um deine Identität zu verlinken."
                                ))
                                .append(TextComponent.fromLegacyText("\n\n§7Unseren TeamSpeak erreichst du über die IP §fts.mcone.eu§7."))
                                .append("§7» §3§l§nKlicke hier um zu joinen")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oTeamSpeak-Client Öffnen").create()))
                                .event(new ClickEvent(Action.OPEN_URL, "http://connect2ts.mcone.eu/"))
                                .append(TextComponent.fromLegacyText("\n§8§m----------------§r§8| §3Teamspeak §8§m|----------------"))
                                .create()
                );
                return;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("unlink")) {
                if (cp.isTeamspeakIdLinked()) {
                    TeamspeakVerifier tsv = BungeeCoreSystem.getSystem().getTeamspeakVerifier();
                    if (tsv != null) tsv.unlink(cp);
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§2Deine Identität wurde erfolgreich von deinem Minecraftaccount entfernt. Benutze §a/ts link§2 um wieder eine Identität zu verlinken.");
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du hast gerade keine TeamSpeak-Identität verlinkt! Benutze §c/ts link§4 um eine Identität zu verlinken.");
                }
                return;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("link")) {
                if (!cp.isTeamspeakIdLinked()) {
                    TeamspeakVerifier tsv = BungeeCoreSystem.getSystem().getTeamspeakVerifier();
                    if (tsv != null) {
                        tsv.sendClientsWithIP(cp);
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Die TeamSpeak Verifizierung ist nicht verfügbar! Bitte melde dies einem Teammitglied.");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du kannst nicht mehr als eine TeamSpeak Identität verlinken!");
                }
                return;
            } else if(args.length == 2 && args[0].equalsIgnoreCase("uidlink")) {
                String tsId = args[1];
                TeamspeakVerifier tsv = BungeeCoreSystem.getSystem().getTeamspeakVerifier();
                tsv.addRegistering(p, tsId);
            }

            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/tc §4oder §c/ts link <Identität-UID> §4oder §c/ts unlink");
        }
    }
}
