/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.friend;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Party {

    public static HashMap<String, Party> parties = new HashMap<>();
    @Getter
    private static final Messenger messenger = BungeeCoreSystem.getSystem().initializeMessenger("system.prefix.party");

    @Getter
    private ProxiedPlayer leader;
    @Getter
    private List<ProxiedPlayer> member;
    @Getter
    private List<ProxiedPlayer> invites;
    @Getter
    private long created;

    public Party(ProxiedPlayer leader, ProxiedPlayer... member) {
        if (leader != null) {
            if (isInParty(leader)) {
                messenger.send(leader, "§4Du bist noch in einer Party! Benutze §c/party leave §4um die Party zu verlassen!");
            } else {
                parties.put(leader.getName().toLowerCase(), this);

                this.leader = leader;
                this.member = new ArrayList<>();
                this.invites = new ArrayList<>();
                this.created = System.currentTimeMillis() / 1000;

                this.member.add(leader);

                messenger.send(leader, "§2Eine neue Party wurde erfolgreich erstellt");
                if (member != null) {
                    for (ProxiedPlayer m : member) {
                        this.invites.add(m);
                        messenger.send(leader, "§f" + m.getName() + "§7 wurde in die Party eingeladen!");
                        m.sendMessage(
                                new ComponentBuilder("")
                                        .append(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.party", CoreSystem.getInstance().getCorePlayer(m))))
                                        .append(TextComponent.fromLegacyText("§f" + this.leader.getName() + "§2 hat dich in seine Party eingeladen!\n"))
                                        .append(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.party", CoreSystem.getInstance().getCorePlayer(m)))
                                        .append("§a[ANNEHMEN]")
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/party accept " + this.leader.getName()).create()))
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + this.leader.getName()))
                                        .create()
                        );
                    }
                }
            }
        } else {
            BungeeCoreSystem.getInstance().sendConsoleMessage("§4Party konnte nicht erstellt werden da (leader == null)");
        }
    }

    public void invite(final ProxiedPlayer p) {
        this.invites.add(p);
        p.sendMessage(
                new ComponentBuilder("")
                        .append(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.party", CoreSystem.getInstance().getCorePlayer(p))))
                        .append(TextComponent.fromLegacyText("§f" + this.leader.getName() + "§2 hat dich in seine Party eingeladen!\n"))
                        .append(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.party", CoreSystem.getInstance().getCorePlayer(p))))
                        .append("§a[ANNEHMEN]")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/party accept " + this.leader.getName()).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + this.leader.getName()))
                        .create()
        );
    }

    public void addPlayer(final ProxiedPlayer p) {
        if (!this.member.contains(p)) {
            if (this.invites.contains(p)) {
                this.member.add(p);
                this.invites.remove(p);
                messenger.send(p, "§2Du bist der Party beigetreten!");
                this.sendAll("§f" + p.getName() + " §7ist der Party beigetreten!");
            } else {
                messenger.send(p, "§4Du wurdest nicht in diese Party eingeladen!");
            }
        } else {
            messenger.send(p, "§4Du bist bereits in dieser Party!");
        }
    }

    public void removePlayer(final ProxiedPlayer p) {
        if (this.leader.equals(p)) {
            if (this.member.size() > 1) {
                this.member.remove(p);
                parties.remove(this.leader.getName().toLowerCase(), this);
                this.leader = this.member.get(0);
                parties.put(this.leader.getName().toLowerCase(), this);

                for (ProxiedPlayer m : this.member) {
                    messenger.send(m, "§c" + p.getName() + " §4hat die Party verlassen");
                    messenger.send(m, "§f" + this.leader + " §7ist jetzt der neue Partyleader!");
                }
            } else {
                destroy();
            }
        } else if (this.member.contains(p)) {
            this.member.remove(p);
            this.sendAll("§c" + p.getName() + " §4hat die Party verlassen");
        }
    }

    public void promotePlayer(final ProxiedPlayer p) {
        parties.remove(this.leader.getName().toLowerCase(), this);
        this.leader = p;
        parties.put(this.leader.getName().toLowerCase(), this);
        this.sendAll("§f" + p.getName() + " §7ist nun der neue Partyleader!");
    }

    public void delete(final ProxiedPlayer by) {
        this.sendAll("§4Die Party wurde von §c" + by.getName() + " §4aufgelöst!");
        parties.remove(this.leader.getName().toLowerCase());
    }

    private void destroy() {
        parties.remove(this.leader.getName().toLowerCase());
    }

    public boolean hasMember(final ProxiedPlayer p) {
        for (ProxiedPlayer m : this.member) {
            if (m.equals(p)) return true;
        }
        return false;
    }

    public void sendAll(final String msg) {
        for (ProxiedPlayer p : this.member) {
            messenger.send(p, msg);
        }
    }


    public static boolean isInParty(ProxiedPlayer p) {
        boolean result = false;
        for (Party party : parties.values()) {
            if (party.getMember().contains(p)) result = true;
        }

        return result;
    }

    public static Party getParty(ProxiedPlayer p) {
        for (Party party : parties.values()) {
            if (party.getMember().contains(p) || party.leader.equals(p)) return party;
        }

        return null;
    }

}
