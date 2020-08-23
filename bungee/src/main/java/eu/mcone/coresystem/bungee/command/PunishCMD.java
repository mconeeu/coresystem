/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.api.core.overwatch.punish.PunishTemplate;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PunishCMD extends Command implements TabExecutor {

    private final Overwatch overwatch;

    public PunishCMD(Overwatch overwatch) {
        super("punish", "system.bungee.overwatch.punish");
        this.overwatch = overwatch;
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), player.getUniqueId()))
                return;

            if (args.length == 2) {
                UUID t = BungeeCoreSystem.getInstance().getPlayerUtils().fetchUuid(args[1]);

                if (t != null) {
                    if (args[0].equalsIgnoreCase("unpunish")) {
                        if (overwatch.getPunishManager().isBanned(t)) {
                            overwatch.getPunishManager().unBan(t);
                            overwatch.getMessenger().send(player, "§2Du hast den Spieler §f" + args[1] + "§2 entbannt!");
                        } else {
                            overwatch.getMessenger().send(player, "§4Der Spieler §c" + args[1] + "§4 ist nicht gebannt!");
                        }

                        return;
                    } else if (args[0].equalsIgnoreCase("unmute")) {
                        if (overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getPunishManager().unMute(t);
                            overwatch.getMessenger().send(player, "§2Du hast den Spieler §f" + args[1] + "§2 entmutet!");
                        } else {
                            overwatch.getMessenger().send(player, "§4Der Spieler §c" + args[1] + " §4ist nicht gemutet!");
                        }

                        return;
                    } else if (args[0].equalsIgnoreCase("check")) {
                        if (overwatch.getPunishManager().isBanned(t) && overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getMessenger().send(player, "§7Der Spieler §f" + args[1] + "§7 ist gebannt und gemuted.");
                        } else if (overwatch.getPunishManager().isBanned(t)) {
                            overwatch.getMessenger().send(player, "§7Der Spieler §f" + args[1] + "§7 ist gebannt.");
                        } else if (overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getMessenger().send(player, "§7Der Spieler §f" + args[1] + "§7 ist gemuted.");
                        } else {
                            overwatch.getMessenger().send(player, "§7Der Spieler §f" + args[1] + "§7 ist weder gebannt noch gemuted.");
                        }

                        return;
                    }
                } else {
                    overwatch.getMessenger().send(player, "§4Der Minecraftaccount §c" + args[1] + "§4 existiert nicht!");
                    return;
                }
            } else if (args.length == 3 || args.length == 4) {
                try {
                    if (!player.getName().equalsIgnoreCase(args[0])) {
                        OfflineCorePlayer targetCorePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(args[0]);
                        if (targetCorePlayer != null) {
                            PunishTemplate template = PunishTemplate.getTemplateByID(args[1]);
                            String reason = args[2];
                            String chatLogID = null;

                            if (args.length == 4) {
                                chatLogID = args[3];
                            }

                            if (!targetCorePlayer.isBanned()) {
                                if (targetCorePlayer.hasPermission("group.team")) {
                                    overwatch.getMessenger().send(player, "§4Du darfst keine Teammitglieder bannen!");
                                } else {
                                    if (template != null) {
                                        if (chatLogID != null) {
                                            //TODO: Implement ChatLog System and check if the ChatLog exists in the DB
                                        }

                                        overwatch.getPunishManager().punishPlayer(targetCorePlayer.getUuid(), template, reason, chatLogID, player.getUniqueId());
                                        overwatch.getMessenger().send(player, "§2Du hast erfolgreich den Spieler §f" + targetCorePlayer.getName() + "§2 bestraft!");

                                        for (ProxiedPlayer members : overwatch.getLoggedIn()) {
                                            if (members != player) {
                                                overwatch.getMessenger().send(members, "§f" + player.getName() + "§7 hat den Spieler §c" + targetCorePlayer.getName() + "§7 mit dem Template §o" + template.getName() + "§7 und dem Grund §e" + reason + "§7 bestraft!");
                                            }
                                        }
                                    } else {
                                        overwatch.getMessenger().send(player, "§4Du kannst ausschließlich diese Templates verwenden:" +
                                                "\n§8» §7CLIENTMODS §8- §eCM" +
                                                "\n§8» §7ERSCHEINEN §8- §eES" +
                                                "\n§8» §7TEAMTROLLING §8- §eTT" +
                                                "\n§8» §7BETRUG §8- §eBT" +
                                                "\n§8» §7BUGUSING §8- §eBU" +
                                                "\n§8» §7RADIKALISMUS §8- §eRM" +
                                                "\n" +
                                                "\n§8» §7MOBBING §8- §eMB" +
                                                "\n§8» §7BELEIDIGUNG §8- §eBL" +
                                                "\n§8» §7DROHUNG §8- §eDH" +
                                                "\n§8» §7SPAM §8- §eSP" +
                                                "\n§8» §7WERBUNG §8- §eWB" +
                                                "\n§8§m--------------------" +
                                                "\n§8» §7Alle Infos zum Punishsystem und zu den verschiedenen Templates findest du im Team Wiki: §fhttps://wiki.onegaming.group/coresystem");
                                    }
                                }
                            } else {
                                overwatch.getMessenger().send(player, "§4Dieser Spieler ist bereits gebannt!");
                            }
                        } else {
                            overwatch.getMessenger().send(player, "§4Dieser Spieler ist nicht online!");
                        }
                    } else {
                        overwatch.getMessenger().send(player, "§7Du kannst dich nicht selbst bannen");
                    }

                    return;
                } catch (PlayerNotResolvedException e) {
                    overwatch.getMessenger().send(player, "§cDer Spieler " + args[0] + " existiert nicht!");
                    return;
                }
            }

            overwatch.getMessenger().send(player, "§cPunish Hilfe:" +
                    "\n§8§m------§r §f§lTemplates §8§m------" +
                    "\n§8» §7CLIENTMODS §8- §eCM" +
                    "\n§8» §7ERSCHEINEN §8- §eES" +
                    "\n§8» §7TEAMTROLLING §8- §eTT" +
                    "\n§8» §7BETRUG §8- §eBT" +
                    "\n§8» §7BUGUSING §8- §eBU" +
                    "\n§8» §7RADIKALISMUS §8- §eRM" +
                    "\n" +
                    "\n§8» §7MOBBING §8- §eMB" +
                    "\n§8» §7BELEIDIGUNG §8- §eBL" +
                    "\n§8» §7DROHUNG §8- §eDH" +
                    "\n§8» §7SPAM §8- §eSP" +
                    "\n§8» §7WERBUNG §8- §eWB" +
                    "\n§8§m------§r §f§lBefehle §8§m------" +
                    "\n§8» §c/punish unban <§cSpieler§c>" +
                    "\n§8» §c/punish unmute <§cSpieler§c>" +
                    "\n§8» §c/punish check <§cSpieler§c>" +
                    "\n§8» §c/punish <§fSpieler§c> <§eTemplateID§c> <§fGrund§c>" +
                    "\n§8§m--------------------" +
                    "\n§8» §7Alle Infos zum Punishsystem und zu den verschiedenen Templates findest du (bald) im Team Wiki: §fhttps://wiki.onegaming.group/coresystem");
        } else {
            overwatch.getMessenger().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        ArrayList<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                result.add(p.getName());
            }

            result.addAll(Arrays.asList("unpunish", "unmute", "check"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("unban") || args[0].equalsIgnoreCase("unmute") || args[0].equalsIgnoreCase("check")) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            } else {
                for (PunishTemplate t : PunishTemplate.values()) {
                    result.add(t.getId());
                }
            }
        }

        return result;
    }

}
