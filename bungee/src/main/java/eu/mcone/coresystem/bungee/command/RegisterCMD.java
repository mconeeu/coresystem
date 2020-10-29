/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import java.util.Random;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class RegisterCMD extends Command {

    public static final Random REGISTER_RANDOM = new Random();
    public static final MongoCollection<Document> MINECRAFT_REGISTER_CODE_COLLECTION = BungeeCoreSystem.getSystem().getMongoDB(Database.ONEGAMING).getCollection("minecraft_register_codes");

    public RegisterCMD() {
        super("register", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId()))
                return;

            try {
                String code = createAndGetNewCode(p.getUniqueId());
                BungeeCoreSystem.getInstance().getMessenger().send(p,
                        new ComponentBuilder("Danke, dass du eine OneGaming ID erstellst!\nDein Register Code lautet: ")
                                .color(ChatColor.GRAY)
                                .append(code)
                                .color(ChatColor.RED).bold(true)
                                .append(" \nKlicke ")
                                .color(ChatColor.GRAY).bold(false)
                                .append("hier")
                                .color(ChatColor.WHITE).bold(true)
                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://id.onegaming.group/register/minecraft"))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oKlicke zum öffnen").create()))
                                .append(", um deine Registrerung abzuschlißen.")
                                .color(ChatColor.GRAY).bold(false)
                                .create()
                );
            } catch (CoreException ingored) {
                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du hast dich bereits registriert!");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public static String createAndGetNewCode(UUID uuid) throws CoreException {
        if (BungeeCoreSystem.getSystem().getMongoDB(Database.ONEGAMING).getCollection("users").find(eq("minecraft_uuid", uuid.toString())).first() == null) {
            String code;
            do {
                code = String.format("%04d", REGISTER_RANDOM.nextInt(10000));
            } while (MINECRAFT_REGISTER_CODE_COLLECTION.find(eq("code", code)).first() != null);

            MINECRAFT_REGISTER_CODE_COLLECTION.updateOne(eq("uuid", uuid.toString()), set("code", code), new UpdateOptions().upsert(true));
            return code;
        } else {
            throw new CoreException("Cannot register user with uuid "+uuid+". OneGaming ID already exists!");
        }
    }

}
