/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.core.util.Random;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;

public class RegisterCMD extends Command{

	public RegisterCMD(){
		super("register", null);
	}

	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer){
            final ProxiedPlayer p = (ProxiedPlayer)sender;
            final long millis = System.currentTimeMillis();

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            BungeeCoreSystem.getInstance().getMySQL(1).selectAsync("SELECT password FROM userinfo WHERE uuid = '" + p.getUniqueId().toString() + "'", rs_main -> {
                try{
                    if (rs_main.next()){
                        if (rs_main.getObject("password") != null){
                            Messager.send(p, "§4Du hast dich bereits registriert!");
                        } else {
                            BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT `uuid` FROM `website_account_token` WHERE `uuid`= '" + p.getUniqueId().toString() + "' AND `type`='register'", rs -> {
                                String token = new Random(16).nextString();

                                try{
                                    if (rs.next()) {
                                        BungeeCoreSystem.getInstance().getMySQL(1).update("UPDATE `website_account_token` SET `timestamp` = '" + millis / 1000 + "', `token` = '" + token + "' WHERE `uuid`='" + p.getUniqueId().toString() + "' AND `type`='register';");
                                        Messager.send(p, "§2Du kannst dich nun auf §fhttps://www.mcone.eu/register.php?token=" + token + " §2registrieren!");
                                    } else {
                                        BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `website_account_token` (uuid, token, timestamp, type) VALUES ('" +  p.getUniqueId().toString() + "', '" +  token + "', '" +  millis / 1000 + "', 'register')");
                                        Messager.send(p, "§2Du kannst dich nun auf §fhttps://www.mcone.eu/register.php?token=" + token + " §2registrieren!");
                                    }
                                }catch (SQLException e){
                                    e.printStackTrace();
                                }
                            });
                        }
                    }

                }catch (SQLException e1){
                    e1.printStackTrace();
                }
            });
        } else {
            Messager.sendSimple(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }

    }

}
