/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.capture.MotionRecorder;
import org.bukkit.entity.Player;

import java.util.*;

public class CaptureCMD extends CorePlayerCommand {

    private final Map<Player, MotionRecorder> recording;

    public CaptureCMD() {
        super("capture", "system.bukkit.world.capture");
        recording = new HashMap<>();
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p.getUniqueId());

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("play")) {
                String captureName = args[1];
                String npcName = args[2];

                if (captureName != null) {
                    eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture capture = CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCapture(captureName);
                    if (capture != null) {
                        if (npcName != null) {
                            CoreWorld w = cp.getWorld();
                            NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(w, npcName);

                            if (npc != null) {
                                if (npc instanceof PlayerNpc) {
                                    if (w.getName().equalsIgnoreCase(capture.getWorld())) {
                                        PlayerNpc playerNpc = (PlayerNpc) npc;
                                        if (playerNpc.playMotionCapture(capture)) {
                                            p.teleport(playerNpc.getData().getLocation().bukkit());
                                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Die Aufnahme mit dem Namen §a" + captureName + " §2wird nun abgespielt!");
                                        } else {
                                            p.teleport(playerNpc.getData().getLocation().bukkit());
                                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§cDer Npc Spielt bereits ein Motion capture ab!");
                                        }
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die Welt des NPCS stimmt nicht mit der Welt der Aufnahme (§c" + capture.getWorld() + "§4) überein!");
                                    }
                                } else {
                                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Der NPC mit dem Namen §c" + npcName + "§4 ist kein PLAYER_NPC, animationen können nur für PLAYER_NPC gesetzt werden!");
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein NPC mit dem Namen §c" + npcName + "§4 existiert nicht in der Welt " + w.getName() + "!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze §4/npc capture play <name> <npc>");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(p, "§cEs konnte keine Aufnahme unter dem Namen §4" + captureName + " §cgefunden werden!");
                    }

                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze §4/npc capture play <name> <npc>");
                }

                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("record")) {
                String name = args[1];

                if (name != null) {
                    if (recording.containsKey(p)) {
                        CoreSystem.getInstance().getMessenger().send(p, "§cDu bist bereits in einer Aufnahme, speichere diese mit /npc record <name>");
                    } else {
                        if (!CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().existsMotionCapture(name)) {
                            MotionRecorder recorder = new MotionRecorder(p, name);
                            recorder.record();
                            recording.put(p, recorder);
                            CoreSystem.getInstance().getMessenger().send(p, "§2Die Aufnahme wurde gestartet, all deine Bewegungen & Interaktionen werden nun aufgezeichnet!");
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cEs existiert bereits ein Capture mit diesem Namen!");
                        }
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cDu bist bereits in einer Aufnahme, speichere diese mit /npc capture start <name>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                if (name != null) {
                    CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().deleteMotionCapture(name);
                    CoreSystem.getInstance().getMessenger().send(p, "§2Die Aufnahme mit dem Namen §a" + name + " §2wurde erfolgreich gelöscht!");
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze: §4/npc capture delete <name>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getCapturePlayer() != null && playerNpc.getCapturePlayer().isPlaying()) {
                                playerNpc.getCapturePlayer().playing(false);
                                CoreSystem.getInstance().getMessenger().send(p, "§aDie Aufnahme wurde §2gestopt!");
                            } else {
                                CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze: §4/capture stop <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("start")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getCapturePlayer() != null && !playerNpc.getCapturePlayer().isPlaying()) {
                                playerNpc.getCapturePlayer().playing(true);
                                CoreSystem.getInstance().getMessenger().send(p, "§aDie Aufnahme wird §2fortgefahren!");
                            } else {
                                CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze: §4/capture stop <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("forward")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getCapturePlayer() != null && playerNpc.getCapturePlayer().isPlaying()) {
                                playerNpc.getCapturePlayer().forward(true);
                                CoreSystem.getInstance().getMessenger().send(p, "§aDie Aufnahme wurde nun vorwährts abgespielt!");
                            } else {
                                CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze: §4/capture forward <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("backward")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getCapturePlayer() != null && playerNpc.getCapturePlayer().isPlaying()) {
                                playerNpc.getCapturePlayer().forward(false);
                                CoreSystem.getInstance().getMessenger().send(p, "§aDie Aufnahme wurde nun ruckwärts abgespielt!");
                            } else {
                                CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cBitte benutze: §4/capture backward <NPC>");
                }

                return true;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture> dataList = CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCaptures();
                if (dataList.size() != 0) {
                    CoreSystem.getInstance().getMessenger().send(p, "§2Es sind folgende Aufnahmen verfügbar...");

                    for (eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture data : dataList) {
                        CoreSystem.getInstance().getMessenger().send(p, "§2Name: §a§l" + data.getName() + " §2Länge: §a§l" + data.getLength() + " §2Aufgenommen von: §a§l" + data.getCreator());
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cEs sind momentan keine Aufnahmen verfügbar!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (recording.containsKey(p)) {
                    MotionRecorder recorder = recording.get(p);
                    recorder.stop();
                    CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().saveMotionCapture(recorder);
                    CoreSystem.getInstance().getMessenger().send(p, "§2Die Aufnahme wurde erfolgreich unter dem Namen §a" + recorder.getName() + " §2gespeichert!");
                    Sound.done(p);
                    recording.remove(p);
                    return true;
                } else {
                    CoreSystem.getInstance().getMessenger().send(p, "§cDu musst eine Aufnahme beginnen um diese speichern zu können!");
                    Sound.cancel(p);
                    return false;
                }
            }
        }

        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                "\n§c/capture play <name> <npc> §4oder " +
                "\n§c/capture record <name>" +
                "\n§c/capture save §4oder " +
                "\n§c/capture delete <name> §4oder " +
                "\n§c/capture start <NPC> §4oder " +
                "\n§c/capture stop <NPC> §4oder " +
                "\n§c/capture forward <NPC> §4oder " +
                "\n§c/capture backward <NPC> §4oder " +
                "\n§c/capture list §4oder "
        );

        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"play", "record", "save", "delete", "start", "stop", "forward", "backward", "list"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }

            return matches;
        } else if (args.length == 2 || args.length == 3) {
            String search = args[args.length-1];
            List<String> matches = new ArrayList<>();

            for (NPC npc : CoreSystem.getInstance().getCorePlayer(p).getWorld().getNPCs()) {
                if (npc.getData().getName().startsWith(search)) {
                    matches.add(npc.getData().getName());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }
}
