/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BukkitCMD extends CoreCommand {

    private enum ReloadType {
        TRANSLATIONS("TranslationManager", () -> BukkitCoreSystem.getInstance().getTranslationManager().reload()),
        PERMISSIONS("PermissionsManager", () -> {
            BukkitCoreSystem.getInstance().getPermissionManager().reload();
            for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                cp.reloadPermissions();
            }
        }),
        WORLDS("WorldManager", () -> BukkitCoreSystem.getInstance().getWorldManager().reload()),
        SCOREBOARD("Scoreboards", () -> BukkitCoreSystem.getInstance().getOnlineCorePlayers().forEach(cp -> cp.getScoreboard().reload())),
        NPCS("NPCs", () -> BukkitCoreSystem.getInstance().getNpcManager().reload()),
        HOLOGRAMS("Hologramme", () -> BukkitCoreSystem.getInstance().getHologramManager().reload());

        private final String name;
        private final Runnable runnable;

        ReloadType(String name, Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }
    }

    public BukkitCMD() {
        super("bukkit");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(
                    "§r" +
                    "\n§8§m---------- §r§3§lMCONE-BukkitCoreSystem §8§m----------" +
                    "\n§8[§7§l!§8] §fSystem §8» §7Entwickelt von §fDieserDominik §7und §frufi" +
                    "\n§r" +
                    "\n§7§oWir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten." +
                    "\n§7§oDeshalb sind auch alle von uns verwendeten Plugins ausschließlich selbst entwickelt!" +
                    "\n§8§m---------- §r§3§lMCONE-BukkitCoreSystem §8§m----------" +
                    "\n§r"
            );
            return true;
        } else if (args[0].equals("reload")) {
            if (sender.hasPermission("system.bukkit.reload")) {

                if (args.length == 1) {
                    for (ReloadType reload : ReloadType.values()) {
                        reload(sender, reload);
                    }
                } else if (args.length == 2) {
                    for (ReloadType reload : ReloadType.values()) {
                        if (args[1].equalsIgnoreCase(reload.name().toLowerCase())) {
                            reload(sender, reload);
                            return true;
                        }
                    }
                }
            } else {
                Msg.sendTransl(sender, "system.command.noperm");
                return false;
            }
        }

        Msg.send(sender, "§4Bitte benutze: §c/bukkit [reload] [<permissions | worlds | translations | scoreboard | npcs | holograms>]");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("system.bukkit.reload")) {
                return Collections.singletonList("reload");
            }
        } else if (args.length == 2) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (ReloadType reloadType : ReloadType.values()) {
                if (reloadType.name().toLowerCase().startsWith(search)) {
                    matches.add(reloadType.name().toLowerCase());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

    private void reload(CommandSender sender, ReloadType reloadType) {
        Msg.sendSuccess(sender, "!["+reloadType.name+"] wird neu geladen...");
        reloadType.runnable.run();
    }

}
