/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class RegisterCMD extends Command {

    public RegisterCMD() {
        super("register", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return;

            Document entry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", p.getUniqueId().toString())).first();
            if (entry != null) {
                if (entry.getString("password") != null) {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du hast dich bereits registriert!");
                } else {
                    /*BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).select("SELECT `id`, `uuid` FROM `website_account_token` WHERE `uuid`= '" + p.getUniqueId().toString() + "' AND `type`='register'", rs -> {
                        String token = new Random(16).nextString();

                        try {
                            if (rs.next()) {
                                BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("UPDATE `website_account_token` SET `timestamp` = '" + System.currentTimeMillis() / 1000 + "', `token` = '" + token + "' WHERE `id`=" + rs.getInt("id"));
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§2Du kannst dich nun auf §fhttps://www.mcone.eu/register.php?token=" + token + " §2registrieren!");
                            } else {
                                BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("INSERT INTO `website_account_token` (`uuid`, `token`, `timestamp`, `type`) VALUES ('" + p.getUniqueId().toString() + "', '" + token + "', '" + System.currentTimeMillis() / 1000 + "', 'register')");
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§2Du kannst dich nun auf §fhttps://www.mcone.eu/register.php?token=" + token + " §2registrieren!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });*/
                }
            }
        } else {
            BungeeCoreSystem.getInstance().getMessager().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }

    }

}
