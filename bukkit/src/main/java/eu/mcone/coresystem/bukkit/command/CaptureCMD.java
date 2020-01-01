package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.capture.MotionRecorder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class CaptureCMD extends CorePlayerCommand {

    private boolean capture;
    private MotionRecorder motionRecorder;

    public CaptureCMD() {
        super("capture", "system.bukkit.world.capture");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p.getUniqueId());

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("play")) {
                String captureName = args[1];
                String npcName = args[2];

                if (captureName != null) {
                    MotionCaptureData data = CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCapture(captureName);
                    if (data != null) {
                        if (npcName != null) {
                            CoreWorld w = cp.getWorld();
                            NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(w, npcName);

                            if (npc != null) {
                                if (npc instanceof PlayerNpc) {
                                    if (w.getName().equalsIgnoreCase(data.getWorld())) {
                                        PlayerNpc playerNpc = (PlayerNpc) npc;
                                        playerNpc.playMotionCapture(data);
                                        p.teleport(playerNpc.getData().getLocation().bukkit());
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Aufnahme mit dem Namen §a" + captureName + " §2wird nun abgespielt!");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Welt des NPCS stimmt nicht mit der Welt der Aufnahme (§c" + data.getWorld() + "§4) überein!");
                                    }
                                } else {
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der NPC mit dem Namen §c" + npcName + "§4 ist kein PLAYER_NPC, animationen können nur für PLAYER_NPC gesetzt werden!");
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Ein NPC mit dem Namen §c" + npcName + "§4 existiert nicht in der Welt " + w.getName() + "!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze §4/npc capture play <name> <npc>");
                        }
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cEs konnte keine Aufnahme unter dem Namen §4" + captureName + " §cgefunden werden!");
                    }

                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze §4/npc capture play <name> <npc>");
                }

                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("record")) {
                String name = args[1];

                if (name != null) {
                    if (capture) {
                        CoreSystem.getInstance().getMessager().send(p, "§cDu bist bereits in einer Aufnahme, speichere diese mit /npc record <name>");
                    } else {
                        capture = true;
                        motionRecorder = new MotionRecorder(p, name);
                        motionRecorder.record();
                        CoreSystem.getInstance().getMessager().send(p, "§2Die Aufnahme wurde gestartet, all deine Bewegungen & Interaktionen werden nun aufgezeichnet!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cDu bist bereits in einer Aufnahme, speichere diese mit /npc capture start <name>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                if (name != null) {
                    MotionCaptureData data = getCapture(name);

                    if (data != null) {
                        CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().deleteMotionCapture(data);
                        CoreSystem.getInstance().getMessager().send(p, "§2Die Aufnahme mit dem Namen §a" + name + " §2wurde erfolgreich gelöscht!");
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cEs konnte keine Aufnahme unter dem Namen §4" + name + " §cgefunden werden!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze: §4/npc capture delete <name>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getMotionPlayer() != null && playerNpc.getMotionPlayer().isPlaying()) {
                                playerNpc.getMotionPlayer().stopPlaying();
                                CoreSystem.getInstance().getMessager().send(p, "§aDie Aufnahme wurde §2gestopt!");
                            } else {
                                CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessager().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze: §4/capture stop <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("start")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getMotionPlayer() != null && !playerNpc.getMotionPlayer().isPlaying()) {
                                playerNpc.getMotionPlayer().startPlaying();
                                CoreSystem.getInstance().getMessager().send(p, "§aDie Aufnahme wird §2fortgefahren!");
                            } else {
                                CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessager().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze: §4/capture stop <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("forward")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getMotionPlayer() != null && playerNpc.getMotionPlayer().isPlaying()) {
                                playerNpc.getMotionPlayer().forward();
                                CoreSystem.getInstance().getMessager().send(p, "§aDie Aufnahme wurde nun vorwährts abgespielt!");
                            } else {
                                CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessager().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze: §4/capture forward <NPC>");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("backward")) {
                String npcName = args[1];
                if (npcName != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), npcName);

                    if (npc != null) {
                        if (npc instanceof PlayerNpc) {
                            PlayerNpc playerNpc = (PlayerNpc) npc;

                            if (playerNpc.getMotionPlayer() != null && playerNpc.getMotionPlayer().isPlaying()) {
                                playerNpc.getMotionPlayer().backward();
                                CoreSystem.getInstance().getMessager().send(p, "§aDie Aufnahme wurde nun ruckwärts abgespielt!");
                            } else {
                                CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessager().send(p, "§cMotion captures können nur auf §4PLAYER_NPC §cangewand werden!");
                        }
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cDer NPC §4" + npcName + " §cspielt momentan keine Aufnahme ab!");
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cBitte benutze: §4/capture backward <NPC>");
                }

                return true;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<MotionCaptureData> dataList = CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCaptures();
                if (dataList.size() != 0) {
                    CoreSystem.getInstance().getMessager().send(p, "§2Es sind folgende Aufnahmen verfügbar...");

                    for (MotionCaptureData data : dataList) {
                        CoreSystem.getInstance().getMessager().send(p, "§2Name: §a§l" + data.getName() + " §2Länge: §a§l" + data.getLength() + " §2Aufgenommen von: §a§l" + data.getCreator());
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cEs sind momentan keine Aufnahmen verfügbar!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (capture) {
                    if (motionRecorder != null) {
                        motionRecorder.stopRecording();
                        CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().saveMotionCapture(motionRecorder);
                        capture = false;
                        CoreSystem.getInstance().getMessager().send(p, "§2Die Aufnahme wurde erfolgreich unter dem Namen §a" + motionRecorder.getName() + " §2gespeichert!");
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                        return true;
                    } else {
                        CoreSystem.getInstance().getMessager().send(p, "§cDu musst eine Aufnahme beginnen um diese speichern zu können!");
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                        return false;
                    }
                } else {
                    CoreSystem.getInstance().getMessager().send(p, "§cDu musst eine Aufnahme beginnen um diese speichern zu können!");
                }

                return true;
            }
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: " +
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

    private MotionCaptureData getCapture(final String name) {
        if (CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().existsMotionCapture(name)) {
            return CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCapture(name);
        } else {
            return null;
        }
    }
}
