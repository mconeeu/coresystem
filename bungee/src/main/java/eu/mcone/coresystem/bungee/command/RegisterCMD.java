/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.util.RandomString;
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

            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            CoreSystem.mysql1.selectAsync("SELECT passwort FROM userinfo WHERE uuid = '" + p.getUniqueId().toString() + "'", rs_main -> {
                try{
                    if (rs_main.next()){
                        if (rs_main.getObject("passwort") != null){
                            Messager.send(p, "§4Du hast dich bereits registriert!");
                        } else {
                            CoreSystem.mysql1.select("SELECT `uuid` FROM `website_account_token` WHERE `uuid`= '" + p.getUniqueId().toString() + "' AND `type`='register'", rs -> {
                                String token = new RandomString(16).nextString();

                                try{
                                    if (rs.next()) {
                                        CoreSystem.mysql1.update("UPDATE `website_account_token` SET `timestamp` = '" + millis / 1000 + "', `token` = '" + token + "' WHERE `uuid`='" + p.getUniqueId().toString() + "' AND `type`='register';");
                                        Messager.send(p, "§2Du kannst dich nun auf §fhttps://www.mcone.eu/register.php?token=" + token + " §2registrieren!");
                                    } else {
                                        CoreSystem.mysql1.update("INSERT INTO `website_account_token` (uuid, token, timestamp, type) VALUES ('" +  p.getUniqueId().toString() + "', '" +  token + "', '" +  millis / 1000 + "', 'register')");
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
            Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }

    }

}
