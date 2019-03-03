/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils.bots.discord.command.utils;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public abstract class DiscordCommand {

    @Getter
    private String name;
    @Getter
    private Role role;
    @Getter
    private List<String> aliases;

    public DiscordCommand(String name, Role role, String... aliases) {
        this.name = name;
        this.role = role;
        this.aliases = Arrays.asList(aliases);
    }

    public void execute(final PrivateMessageReceivedEvent e, String[] args) {
        User user = e.getAuthor();
        Member member = null;

        if (!user.isBot()) {
            for (Guild guild : user.getJDA().getGuilds()) {
                member = guild.getMemberById(user.getId());
            }

            if (member != null) {
                int i = 0;
                for (int var = role.getHierarchyID(); var >= 1; var--) {
                    if (i <= 12) {
                        if (role.getName().equalsIgnoreCase(Role.EVERYONE.getName())
                                || role.getName().equalsIgnoreCase(Role.SPIELER.getName())) {
                            onCommand(member, args, e);
                            break;
                        } else if (member.getRoles().contains(member.getGuild().getRolesByName(role.getRoleByHierarchyID(var).getName(), true).get(0))) {
                            onCommand(member, args, e);
                            break;
                        }
                        i++;
                    } else {
                        e.getMessage().getPrivateChannel().sendMessage("Du hast keine Berechtigung diesen Befehl auszuführen.").queue();
                        break;
                    }
                }

                /* OLD
                int i = 0;
                for (Role roles : roles) {
                    if (i < this.roles.size()) {
                        int var = roles.getHierarchyID();
                        if (i != 0)
                            var = var - i;

                        System.out.println(member.getGuild().getRolesByName(roles.getRoleByHierarchyID(var).getName(), true).get(0));

                        if (member.getRoles().contains(member.getGuild().getRolesByName(roles.getRoleByHierarchyID(var).getName(), true).get(0))) {
                            System.out.println("True");
                            onCommand(member, shortMessage.split(" "), e);
                            break;
                        }

                        i++;
                    } else {
                        e.getMessage().getPrivateChannel().sendMessage("Du hast keine Berechtigung diesen Befehl auszuführen.").queue();
                        break;
                    }
                }
                **/
            } else {
                e.getMessage().getPrivateChannel().sendMessage("").queue();
            }
        }
    }

    public abstract boolean onCommand(final Member member, final String[] args, PrivateMessageReceivedEvent e);

}
