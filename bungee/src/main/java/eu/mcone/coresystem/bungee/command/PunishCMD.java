/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

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
        super("punish", "overwatch.punish");
        this.overwatch = overwatch;
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    overwatch.getMessenger().send(p, "§2BannSystem Hilfe:" +
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
                            "\n§8» §c/ban unban <§cSpieler§c>" +
                            "\n§8» §c/ban unmute <§cSpieler§c>" +
                            "\n§8» §c/ban check <§cSpieler§c>" +
                            "\n§8» §c/ban <§fSpieler§c> <§eTemplateID§c> <§fGrund§c>" +
                            "\n§8§m--------------------" +
                            "\n§8» §7Alle Infos zum Bannsystem und zu den verschiedenen Templates findest du (bald) im Team Wiki: §fhttps://www.mcone.eu/dashboard/wiki.php");
                } else {
                    overwatch.getMessenger().send(p, "§4Bitte benutze §c/punish help");
                }
            } else if (args.length == 2) {
                UUID t = BungeeCoreSystem.getInstance().getPlayerUtils().fetchUuid(args[1]);

                if (t != null) {
                    if (args[0].equalsIgnoreCase("unban")) {
                        if (overwatch.getPunishManager().isBanned(t)) {
                            overwatch.getPunishManager().unBan(t);
                            overwatch.getMessenger().send(p, "§2Du hast den Spieler §f" + args[1] + "§2 entbannt!");
                        } else {
                            overwatch.getMessenger().send(p, "§4Der Spieler §c" + args[1] + "§4 ist nicht gebannt!");
                        }
                    } else if (args[0].equalsIgnoreCase("unmute")) {
                        if (overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getPunishManager().unMute(t);
                            overwatch.getMessenger().send(p, "§2Du hast den Spieler §f" + args[1] + "§2 entmutet!");
                        } else {
                            overwatch.getMessenger().send(p, "§4Der Spieler §c" + args[1] + " §4ist nicht gemutet!");
                        }
                    } else if (args[0].equalsIgnoreCase("check")) {
                        if (overwatch.getPunishManager().isBanned(t) && overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getMessenger().send(p, "§7Der Spieler §f" + args[1] + "§7 ist gebannt und gemuted");
                        } else if (overwatch.getPunishManager().isBanned(t)) {
                            overwatch.getMessenger().send(p, "§7Der Spieler §f" + args[1] + "§7 ist gebannt");
                        } else if (overwatch.getPunishManager().isMuted(t)) {
                            overwatch.getMessenger().send(p, "§7Der Spieler §f" + args[1] + "§7 ist gemuted");
                        } else {
                            overwatch.getMessenger().send(p, "§7Der Spieler §f" + args[1] + "§7 ist weder gebannt noch gemuted");
                        }
                    } else {
                        overwatch.getMessenger().send(p, "§4Bitte benutze §c/ban help");
                    }
                } else {
                    overwatch.getMessenger().send(p, "§4Der Minecraftaccount §c" + args[1] + "§4 existiert nicht!");
                }
            } else if (args.length == 3) {
                ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);
                PunishTemplate template = PunishTemplate.getTemplateByID(args[1]);
                String grund = args[2];

                if (t == null) {
                    overwatch.getMessenger().send(p, "§4Dieser Spieler ist nicht online!");
                } else if (t == p) {
                    overwatch.getMessenger().send(p, "§7Du kannst dich nicht selbst bannen");
                } else {
                    if (!overwatch.getPunishManager().isBanned(t.getUniqueId())) {
                        if (t.hasPermission("group.team")) {
                            overwatch.getMessenger().send(p, "§4Du darfst keine Teammitglieder bannen!");
                        } else {
                            if (template != null) {
                                overwatch.getPunishManager().punishPlayer(t.getUniqueId(), template, grund, p.getUniqueId());

                                overwatch.getMessenger().send(p, "§2Du hast erfolgreich den Spieler §f" + t.getName() + "§2 gebannt!");
                                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                    if (player.hasPermission("system.bungee.ban")) {
                                        if (player != p) {
                                            overwatch.getMessenger().send(p, "§f" + p.getName() + "§7 hat den Spieler §c" + t.getName() + "§7 mit dem Template §o" + template.getName() + "§7 und dem Grund §e" + grund + "§7 gebannt!");
                                        }
                                    }
                                }
                            } else {
                                overwatch.getMessenger().send(p, "§4Du kannst ausschließlich diese Templates verwenden:" +
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
                                        "\n§8» §7Alle Infos zum Bannsystem und zu den verschiedenen Templates findest du im Team Wiki: §fhttps://www.mcone.eu/dashboard/wiki.php");
                            }
                        }
                    } else {
                        overwatch.getMessenger().send(p, "§4Dieser Spieler ist bereits gebannt!");
                    }
                }
            } else {
                overwatch.getMessenger().send(p, "§4Bitte benutze §c/punish help");
            }
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

            result.addAll(Arrays.asList("unban", "unmute", "check"));
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
