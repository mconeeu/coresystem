/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WartungCMD extends Command{

public WartungCMD(){
  super("wartung", "system.bungee.wartung");
}

    public void execute(final CommandSender sender, final String[] args){
        if(sender instanceof ProxiedPlayer){
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length == 0){
                Messager.send(p, "§8§m------------------------------------------");

                if (BungeeCoreSystem.sqlconfig.getLiveBooleanConfigValue("Wartungs-Modus")) {
                    Messager.send(p, "§2Der Wartungsmodus ist aktiviert");
                } else {
                    Messager.send(p, "§4Der Wartungsmodus ist deaktiviert");
                }

                Messager.sendSimple(p, BungeeCoreSystem.sqlconfig.getConfigValue("System-Prefix"));
                Messager.send(p, "§7Wartungsmodus aktivieren §f/wartung on");
                Messager.send(p, "§7Wartungsmodus deaktivieren §f/wartung off");
                Messager.send(p,  "§8§m------------------------------------------");

                return;
            }

            if (args.length == 1){
                if (p.hasPermission("system.bungee.wartung")){
                    if (args[0].equalsIgnoreCase("on")) {
                        if (!BungeeCoreSystem.sqlconfig.getLiveBooleanConfigValue("Wartungs-Modus")) {
                            BungeeCoreSystem.sqlconfig.updateMySQLConfig("Wartungs-Modus", true);
                            Messager.send(p, "§2Wartungsmodus aktiviert!");
                        }else{
                            Messager.send(p, "§4Der Wartungsmodus ist bereits aktiviert!");
                        }
                    } else if (args[0].equalsIgnoreCase("off")) {
                        if (BungeeCoreSystem.sqlconfig.getLiveBooleanConfigValue("Wartungs-Modus")) {
                            BungeeCoreSystem.sqlconfig.updateMySQLConfig("Wartungs-Modus", false);
                            Messager.send(p, "§2Wartungsmodus deaktiviert!");
                        }else{
                            Messager.send(p, "§4Der Wartungsmodus ist nicht aktiviert!");
                        }
                    }
                }else{
                    Messager.send(p, BungeeCoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
                }
            }
        }else{
             Messager.send(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }
}
