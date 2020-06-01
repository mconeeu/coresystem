/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.discord.DiscordControlBot;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class DiscordCMD extends Command {

    public DiscordCMD() {
        super("discord", null, "Discord");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

            if (args.length == 0) {
                p.sendMessage(
                        new ComponentBuilder("")
                                .append(TextComponent.fromLegacyText("§8§m----------------§r§8| §3Discord §8§m|----------------§r"))
                                .append(TextComponent.fromLegacyText(
                                        cp.isDiscordIdLinked() ?
                                                "\n§7Du hast diese Discord ID verlinkt: §f" + cp.getDiscordUid() + "\n§7Benutze §3/discord unlink§7 zum entfernen" :
                                                "\n§7§oDu hast keine Discord Identität verlinkt!\n§2Benutze §a/discord link <Discord-Name>§2 um deine Identität zu verlinken."
                                ))
                                .append(TextComponent.fromLegacyText("\n\n§7Unseren Discord erreichst du über den einladungscode b6BXpdt"))
                                .append("§7» §3§l§nKlicke hier um zu joinen")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oDiscord-Client Öffnen").create()))
                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://discordapp.com/invite/b6BXpdt"))
                                .append(TextComponent.fromLegacyText("§8§m----------------§r§8| §3Discord §8§m|----------------"))
                                .create()
                );
            } else if (args.length == 2 && args[0].equalsIgnoreCase("link")) {
                if (!cp.isDiscordIdLinked()) {
                    if (!BungeeCoreSystem.getSystem().getDiscordControlBot().getRegistering().containsValue(p.getUniqueId())) {
                        DiscordControlBot dv = BungeeCoreSystem.getSystem().getDiscordControlBot();
                        if (dv != null) {
                            if (args[1] != null) {
                                dv.register(p, args[1]);
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Die Discord Verifizierung ist nicht verfügbar! Bitte melde dies einem Teammitglied.");
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du bist bereits im Verifizierungsvorgang!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du kannst nicht mehr als eine Discord Identität verlinken!");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("unlink")) {
                if (cp.isDiscordIdLinked()) {
                    if (!BungeeCoreSystem.getSystem().getDiscordControlBot().getRegistering().containsValue(p.getUniqueId())) {
                        DiscordControlBot dv = BungeeCoreSystem.getSystem().getDiscordControlBot();
                        if (dv != null) {
                            Document document = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", cp.getUuid().toString())).first();
                            if (document != null) {
                                dv.unlink(document.getString("discord_uid"), cp);
                            } else {
                                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Es ist ein Datenbank fehler aufgetreten, bitte melde dies einem MCONE Teammitglied!");
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Die Discord Verifizierung ist nicht verfügbar! Bitte melde dies einem Teammitglied.");
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du bist noch im Verifizierungsvorgang!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du hast dich noch nicht verifiziert!");
                }
            }
        }
    }
}
