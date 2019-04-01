/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils.bots.discord.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.discord.DiscordControlBot;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.DiscordCommand;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.Role;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;

import java.util.Map;
import java.util.UUID;

public class EventListener extends ListenerAdapter {

    private DiscordControlBot discordControlBot = null;

    // CLIENT RECEIVED MESSAGES
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        CorePlayer cp = null;
        User user = e.getAuthor();
        Message message = e.getMessage();
        String rawMessage = message.toString();

        rawMessage = rawMessage
                .replace("M:" + user.getName(), "")
                .replace("#" + user.getDiscriminator() + ":", "")
                .replace("(", "")
                .replace(")", "")
                .replace(message.getId(), "");

        if (!user.isBot()) {
            if (rawMessage.contains("mcone:")) {
                rawMessage = rawMessage.replace("mcone:", "");
                String[] args = rawMessage.split(" ");

                System.out.println("ARGS LENGTH: " + args.length);

                int i = 0;
                for (Map.Entry<String, DiscordCommand> entry : BungeeCoreSystem.getSystem().getDiscordControlBot().getCommands().entrySet()) {
                    for (String aliases : entry.getValue().getAliases()) {
                        if (i < entry.getValue().getAliases().size()) {
                            if (aliases.equalsIgnoreCase(args[0])) {
                                entry.getValue().execute(e, args);
                                break;
                            }
                        } else {
                            message.getPrivateChannel().sendMessage("Der Befehl existiert nicht").queue();
                            break;
                        }
                    }
                }
            } else if (discordControlBot.getRegistering().containsKey(user.getName())) {
                for (CorePlayer player : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    if (player.getName().equalsIgnoreCase(rawMessage)) cp = player;
                }

                if (cp != null) {
                    discordControlBot.link(user, cp);
                    discordControlBot.getRegistering().remove(user.getName());
                } else {
                    for (Map.Entry<String, UUID> entry : discordControlBot.getRegistering().entrySet()) {
                        BungeeCoreSystem.getSystem().getMessager().send(ProxyServer.getInstance().getPlayer(entry.getValue()), "§4Der Verknüpfungsvorgang wurde abgenbrochen, da der angegebene Spieler mit dem Namen " + rawMessage + " nicht existiert oder online ist.");
                        message.getPrivateChannel().sendMessage("*Der Verknüpfungsvorgang wurde abgenbrochen, da der angegebene Spieler mit dem Namen* **" + rawMessage + "** *nicht existiert oder online ist.*").queue();
                    }

                    discordControlBot.getRegistering().remove(user.getName());
                }
            } else {
                message.getPrivateChannel().sendMessage("Bitte benutze: mcone:help").queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        User user = e.getAuthor();
        Message message = e.getMessage();

        if (!user.isBot()) {
            if (e.isFromType(ChannelType.VOICE)) {
                message.getPrivateChannel().sendMessage("Es tut uns leid, aber dies ist nur ein TextChannelBot.").queue();
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        VoiceChannel vc = e.getChannelJoined();
        Member joinedMember = e.getMember();
        User user = e.getMember().getUser();

        if (vc.getName().contains("support")) {
            for (Member members : e.getGuild().getMembers()) {
                if (members.getOnlineStatus().equals(OnlineStatus.ONLINE)) {
                    System.out.println(members.getUser().getName());

                    int i = 0;
                    for (int var = Role.SUPPORTER.getHierarchyID(); var >= 1; var--) {
                        if (i <= 12) {
                            if (!joinedMember.getRoles().contains(members.getGuild().getRolesByName(Role.SUPPORTER.getRoleByHierarchyID(var).getName(), true).get(0))) {
                                if ((members.getRoles().contains(members.getGuild().getRolesByName(Role.SUPPORTER.getName(), true).get(0)) ||
                                        members.getRoles().contains(members.getGuild().getRolesByName(Role.SUPPORTER.getRoleByHierarchyID(var).getName(), true).get(0)))) {

                                    user.openPrivateChannel().queue((channel) -> channel.sendMessage("» **MCONE** *Support System* «\n" +
                                            "**Der Discord User** *" + joinedMember.getUser().getName() + "* **benötigt Support.**\n" +
                                            "**Mach deine Arbeit!**"
                                    ).queue());
                                    System.out.println("2");
                                    break;
                                } else {
                                    i++;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    // CLIENT INFORMATION
    @Override
    public void onReady(ReadyEvent e) {
        discordControlBot = BungeeCoreSystem.getSystem().getDiscordControlBot();
        BungeeCoreSystem.getSystem().sendConsoleMessage("§aDiscord bot started...");
    }

    @Override
    public void onReconnect(ReconnectedEvent e) {
        BungeeCoreSystem.getSystem().sendConsoleMessage("§cReconnecting to discord server...");
    }

    @Override
    public void onDisconnect(DisconnectEvent e) {
        BungeeCoreSystem.getSystem().sendConsoleMessage("§cDisconnecting from discord server...");
    }

    @Override
    public void onShutdown(ShutdownEvent e) {
        BungeeCoreSystem.getSystem().sendConsoleMessage("§cShutting discord bot down, ShutdownTime: " + e.getShutdownTime());
    }

    @Override
    public void onException(ExceptionEvent e) {
        BungeeCoreSystem.getSystem().sendConsoleMessage("§cDiscord bot exception, cause: " + e.getCause());
    }
}
