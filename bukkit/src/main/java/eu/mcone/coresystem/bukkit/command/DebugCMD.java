package eu.mcone.coresystem.bukkit.command;

import com.google.gson.JsonParseException;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

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
                        CoreSystem.getInstance().getMessenger().sendError(p, "Es wurde keine debug targets registriert!");
                    } else {
                        CoreSystem.getInstance().getMessenger().sendSimple(p, "§7Folgende debug targets wurde registriert...");

                        for (String target : CoreSystem.getInstance().getDebugger().getTargets()) {
                            CoreSystem.getInstance().getMessenger().sendSimple(p, "§8» §7" + target);
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
                        CoreSystem.getInstance().getMessenger().send(p, "§7Du erhälst nun von " + (targets.length > 1 ? "den angebenen targets" : "dem angebenen target") + " alle debug Nachrichten!");
                    } else {
                        CoreSystem.getInstance().getMessenger().sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("hide")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    String notFound = CoreSystem.getInstance().getDebugger().existsTargets(targets);
                    if (notFound == null) {
                        CoreSystem.getInstance().getDebugger().removeViewerTargets(p, targets);
                        CoreSystem.getInstance().getMessenger().send(p, "§7Du erhälst nun von " + (targets.length > 1 ? "den angebenen targets" : "dem angebenen target") + " keine Nachrichten mehr!");
                    } else {
                        CoreSystem.getInstance().getMessenger().sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    CoreSystem.getInstance().getMessenger().send(p, "§7Du erfolgreich §f§l" + targets.length + buildText(targets.length) + " §ahinzugefügt!");
                    CoreSystem.getInstance().getDebugger().registerTargets(targets);
                }

                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                String[] targets = parseToStringArray(p, args[1]);

                if (targets != null) {
                    String notFound = CoreSystem.getInstance().getDebugger().existsTargets(targets);
                    if (notFound == null) {
                        CoreSystem.getInstance().getMessenger().send(p, "§7Du erfolgreich §f§l" + targets.length + buildText(targets.length) + " §centfernt!");
                        CoreSystem.getInstance().getDebugger().removeTargets(targets);
                    } else {
                        CoreSystem.getInstance().getMessenger().sendError(p, "Das debug target " + notFound + " existiert nicht!");
                    }
                }

                return true;
            }
        }

        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                "\n§c/debug §4oder " +
                "\n§c/debug add <target> §4oder " +
                "\n§c/debug remove <target> §4oder " +
                "\n§c/debug show [targets] §4oder " +
                "\n§c/debug hide [targets] §4oder " +
                "\n§c/debug list"
        );

        return false;
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
                CoreSystem.getInstance().getMessenger().sendError(player, "Bitte gibt die debug targets in JSON-Notation an §8[§f1§8, §f2§8]");
                return null;
            }
        }

        CoreSystem.getInstance().getMessenger().sendError(player, "Bitte gib ein oder mehrere debug targets an!");
        return null;
    }
}
