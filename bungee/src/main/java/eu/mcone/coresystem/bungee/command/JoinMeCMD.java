package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JoinMeCMD extends CorePlayerCommand {

    private static class JoinMeEntry {
        private final long started;
        private boolean valid;

        private JoinMeEntry() {
            this.started = System.currentTimeMillis() / 1000;
            this.valid = true;
        }
    }

    private final static Map<UUID, JoinMeEntry> JOIN_MES = new HashMap<>();

    public JoinMeCMD() {
        super("joinme", "system.bungee.joinme");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (!hasCooldown(p.getUniqueId()) || p.hasPermission("system.bungee.cooldown.joinme.cooldown")) {
            if (p.getServer() != null) {
                JOIN_MES.put(p.getUniqueId(), new JoinMeEntry());

                BaseComponent[] message = new ComponentBuilder("§8[§7§l!§8]§7 JoinMe §8> ")
                        .append(p.getName())
                        .color(ChatColor.AQUA)
                        .bold(true)
                        .append(" spielt auf ")
                        .bold(false)
                        .color(ChatColor.DARK_AQUA)
                        .append(p.getServer().getInfo().getName())
                        .underlined(true)
                        .append("!")
                        .underlined(false)
                        .append("\n  ")
                        .append("Jetzt beitreten")
                        .color(ChatColor.GREEN)
                        .bold(true)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7§oKlicke zum Server wechseln")))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join "+p.getName()))
                        .create();

                for (CorePlayer cp : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    if (cp.bungee().getServer() == null || !cp.bungee().getServer().getInfo().equals(p.getServer().getInfo()) || cp.bungee() == p) {
                        switch (cp.getSettings().getJoinMeMessages()) {
                            case NOBODY: {
                                if (cp.bungee() != p) {
                                    continue;
                                }
                            }
                            case FRIENDS: {
                                if (!cp.getFriendData().getFriends().containsKey(p.getUniqueId()) && cp.bungee() != p) {
                                    continue;
                                }
                            }
                            case ALL: {
                                CoreSystem.getInstance().getMessenger().sendSimple(cp.bungee(), message);
                            }
                        }
                    }
                }
            }
        } else {
            CoreSystem.getInstance().getMessenger().sendError(p, "Du kannst nur alle 15 Minuten JoinMe benutzen!");
        }
    }

    public static boolean hasCooldown(UUID uuid) {
        JoinMeEntry entry = JOIN_MES.get(uuid);
        return entry != null && ((System.currentTimeMillis() / 1000) < entry.started + (15 * 60));
    }

    public static boolean hasValidJoinMe(UUID uuid) {
        JoinMeEntry entry = JOIN_MES.get(uuid);
        return entry != null && entry.valid && (System.currentTimeMillis() / 1000) < (entry.started + (10 * 60));
    }

    public static void invalidateJoinMe(UUID uuid) {
        JoinMeEntry entry = JOIN_MES.get(uuid);
        if (entry != null) {
            entry.valid = false;
        }
    }

}
