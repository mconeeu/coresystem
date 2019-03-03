/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils.bots.discord;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.Help;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.DiscordCommand;
import eu.mcone.coresystem.bungee.utils.bots.discord.listener.EventListener;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

//TODO: Make DiscordControlBot as NetworkHandler module
public class DiscordControlBot {

    private final JDA verifierInstance;
    @Getter
    private Map<String, UUID> registering;
    @Getter
    private Map<String, DiscordCommand> commands;

    public DiscordControlBot() throws LoginException {
        //OTHER STUFF
        registering = new HashMap<>();
        commands = new HashMap<>();

        //DISCORD BOT BUILDER
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(DiscordToken.API.getApiToken());
        builder.addEventListener(new EventListener());
        builder.setAutoReconnect(true);

        builder.setStatus(OnlineStatus.ONLINE);
        //builder.setActivity(Activity.playing("on MCONE.EU"));

        verifierInstance = builder.build();

        //COMMANDS
        registerCommand(new Help());
    }

    public void register(final ProxiedPlayer p, final String discordName) {
        User user = null;
        Document document = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", p.getUniqueId().toString())).first();
        if (document != null) {
            for (Guild guild : verifierInstance.getGuilds()) {
                user = guild.getJDA().getUsersByName(discordName, true).get(0);
            }

            if (user != null) {
                BungeeCoreSystem.getInstance().getMessager().send(p, "§2Bitte wechsle zu deinem Discord Fenster und gib in dem gerade vom §fMCONE ControlBot§2 geöffneten Chat deinen §aMinecraft-Namen§2 ein, um den Verifizierungsvorgang abzuschließen.");
                user.openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("» **MCONE** *Verifizierungs System* «\n" +
                            "*Um die Verknüpfung deines Minecraftaccounts abzuschließen gib bitte hier deinen* **Minecraft Namen** *an.*").queue();
                });
                registering.put(discordName, p.getUniqueId());
            } else {
                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Es konnte kein User mit dem Discord Namen " + discordName + " gefunden werden, der Verifizierungsvorgang wurde abgebrochen.");
            }
        } else {
            BungeeCoreSystem.getSystem().getMessager().send(p, "§4Es ist ein Datenbank Fehler aufgetreten, bitte melde dies einem McOne Teammitglied!");
        }
    }

    public void link(final User user, final CorePlayer cp) {
        if (user != null) {
            for (Guild guild : user.getMutualGuilds()) {
                Member member = guild.getMember(user);
                Role role = guild.getRolesByName(eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.Role.VERIFIED.getName(), true).get(0);
                guild.getController().addSingleRoleToMember(member, role).queue();
            }

            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", cp.getUuid().toString()), set("discord_uid", user.getId()));

            user.openPrivateChannel().queue((channel) -> channel.sendMessage("» **MCONE** *Verifizierungs System* «\n" +
                    "Du hast deine Discord Identität erfolgreich mit deinem Minecraftaccount verknüpft!").queue());
            BungeeCoreSystem.getInstance().getMessager().send(cp.bungee(), "§2Deine Discord Identität wurde erfolgreich verknüpft!");

        } else {
            BungeeCoreSystem.getSystem().sendConsoleMessage("§4ERROR discord user is null");
        }
    }

    public void unlink(final String discord_uid, final CorePlayer cp) {
        User user = verifierInstance.getUserById(discord_uid);
        Member member;

        if (user != null) {
            for (Guild guild : user.getMutualGuilds()) {
                member = guild.getMember(user);
                Role role = guild.getRolesByName(eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.Role.VERIFIED.getName(), true).get(0);
                guild.getController().removeSingleRoleFromMember(member, role).queue();
            }

            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", cp.getUuid().toString()), set("discord_uid", ""));

            user.openPrivateChannel().queue((channel) -> channel.sendMessage("» **MCONE** *Verifizierungs System* «\n" +
                    "Deine Discord Identität wurde erfolgreich von deinem Minecraftaccount entfernt.").queue());
            BungeeCoreSystem.getInstance().getMessager().send(cp.bungee(), "§2Deine Discord Identität wurde erfolgreich von deinem Minecraftaccount entfernt. Benutze §a/discord link <Discord-Name>§2 um wieder deine Discord Identität zu verlinken.");
        }
    }

    public void registerCommand(DiscordCommand discordCommand) {
        if (!commands.containsKey(discordCommand.getName())) {
            commands.put(discordCommand.getName(), discordCommand);
        } else {
            BungeeCoreSystem.getInstance().sendConsoleMessage("§4Der Discord Befehl §7" + discordCommand.getName() + " §4wurde bereits registriert.");
        }
    }

    public void shutdown() {
        verifierInstance.shutdown();
    }

    public void forceShutdown() {
        verifierInstance.shutdownNow();
    }
}
