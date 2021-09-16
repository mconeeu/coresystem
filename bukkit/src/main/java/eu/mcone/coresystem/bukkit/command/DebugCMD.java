package eu.mcone.coresystem.bukkit.command;

import com.google.gson.JsonParseException;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebugCMD extends CorePlayerCommand {

    public DebugCMD() {
        super("debug", "system.bukkit.debug");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            CoreSystem.getInstance().getDebugger().openDebuggerInventory(p);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!args[0].equalsIgnoreCase("help")) {
                    if (CoreSystem.getInstance().getDebugger().getTargets().isEmpty()) {
                        Msg.sendError(p, "Es wurde keine debug targets registriert!");
                    } else {
                        Msg.sendSimple(p, "§7Folgende debug targets wurde registriert...");

                        for (String target : CoreSystem.getInstance().getDebugger().getTargets()) {
                            Msg.sendSimple(p, "§8» §7" + target);
                        }
                    }

                    return true;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("show")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    String notFound = CoreSystem.getInstance().getDebugger().existsTargets(targets);
                    if (notFound == null) {
                        CoreSystem.getInstance().getDebugger().registerViewerTargets(p, targets);
                        Msg.send(p, "§7Du erhälst nun von " + (targets.length > 1 ? "den angebenen targets" : "dem angebenen target") + " alle debug Nachrichten!");
                    } else {
                        Msg.sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("hide")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    String notFound = CoreSystem.getInstance().getDebugger().existsTargets(targets);
                    if (notFound == null) {
                        CoreSystem.getInstance().getDebugger().removeViewerTargets(p, targets);
                        Msg.send(p, "§7Du erhälst nun von " + (targets.length > 1 ? "den angebenen targets" : "dem angebenen target") + " keine Nachrichten mehr!");
                    } else {
                        Msg.sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    Msg.send(p, "§7Du erfolgreich §f§l" + targets.length + buildText(targets.length) + " §ahinzugefügt!");
                    CoreSystem.getInstance().getDebugger().registerTargets(targets);
                }

                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    String notFound = CoreSystem.getInstance().getDebugger().existsTargets(targets);
                    if (notFound == null) {
                        Msg.send(p, "§7Du erfolgreich §f§l" + targets.length + buildText(targets.length) + " §centfernt!");
                        CoreSystem.getInstance().getDebugger().removeTargets(targets);
                    } else {
                        Msg.sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            }
        }

        Msg.send(p, "§4Bitte benutze: " +
                "\n§c/debug §4oder " +
                "\n§c/debug add <target> §4oder " +
                "\n§c/debug remove <target> §4oder " +
                "\n§c/debug show [targets] §4oder " +
                "\n§c/debug hide [targets] §4oder " +
                "\n§c/debug list"
        );

        return false;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"add", "remove", "show", "hide", "list"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }

            return matches;
        } else if (args.length >= 2) {
            String search = args[args.length-1];
            List<String> matches = new ArrayList<>();

            for (String target : BukkitCoreSystem.getSystem().getDebugger().getTargets()) {
                if (target.startsWith(search)) {
                    matches.add(target);
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

    private String buildText(int length) {
        return (length > 1 ? " §7debug targets" : " §7debug target");
    }

    private String[] parseToStringArray(Player player, String rawTargets) {
        if (!rawTargets.isEmpty()) {
            try {
                String[] targets = CoreSystem.getInstance().getGson().fromJson(rawTargets, String[].class);

                if (targets.length != 0) {
                    return targets;
                }
            } catch (JsonParseException e) {
                Msg.sendError(player, "Bitte gibt die debug targets in JSON-Notation an §8[§f1§8, §f2§8]");
                return null;
            }
        }

        Msg.sendError(player, "Bitte gib ein oder mehrere debug targets an!");
        return null;
    }
}
