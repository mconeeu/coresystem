/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.capture;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder;
import eu.mcone.coresystem.api.bukkit.npc.capture.codecs.*;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.core.exception.MotionCaptureAlreadyExistsException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotDefinedException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotFoundException;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MotionCaptureHandler implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler {

    private static final MongoCollection<Document> MOTION_CAPTURE_COLLECTION = CoreSystem.getInstance().getMongoDB().getCollection("motion_capture");

    private final HashMap<String, MotionCapture> cache;

    @Getter
    private final MotionCaptureScheduler motionCaptureScheduler;
    @Getter
    private final CodecRegistry codecRegistry;

    public MotionCaptureHandler() {
        cache = new HashMap<>();
        motionCaptureScheduler = new MotionCaptureScheduler();
        codecRegistry = CoreSystem.getInstance().createCodecRegistry(false);

        codecRegistry.registerCodec(PacketPlayInEntityAction.class, PlayInEntityActionCodec.class);
        codecRegistry.registerCodec(PlayerItemHeldEvent.class, ItemSwitchEventCodec.class);
        codecRegistry.registerCodec(PacketPlayInUseEntity.class, PlayInUseCodec.class);
        codecRegistry.registerCodec(PacketPlayOutAnimation.class, PlayOutAnimationCodec.class);
        codecRegistry.registerCodec(PlayerMoveEvent.class, PlayerMoveEventCodec.class);
    }

    /**
     * Loads all motion captures from the database and stores it locally
     */
    public void loadDatabase() {
        for (Document document : MOTION_CAPTURE_COLLECTION.find()) {
            cache.put(document.getString("name"), new MotionCapture(document, codecRegistry));
        }
    }

    /**
     * save the give MotionRecorder in the database
     *
     * @param recorder MotionRecorder
     * @return boolean
     */
    public boolean saveMotionCapture(MotionRecorder recorder) {
        try {
            if (!cache.containsKey(recorder.getName())) {
                if (MOTION_CAPTURE_COLLECTION.find(eq("name", recorder.getName())).first() == null) {
                    if (!recorder.isStop()) {
                        recorder.stop();
                    }

                    MotionCapture capture = new MotionCapture(recorder);
                    cache.put(capture.getName(), capture);
                    MOTION_CAPTURE_COLLECTION.insertOne(capture.toDocument());
                    return true;
                } else {
                    throw new MotionCaptureAlreadyExistsException();
                }
            } else {
                throw new MotionCaptureAlreadyExistsException();
            }
        } catch (MotionCaptureAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns an MotionCaptureData for the given name
     *
     * @param name String (MotionCapture Name)
     * @return MotionCaptureData
     */
    public MotionCapture getMotionCapture(final String name) {
        try {
            if (cache.containsKey(name)) {
                return cache.get(name);
            } else {
                Document document = MOTION_CAPTURE_COLLECTION.find(eq("name", name)).first();

                if (document != null) {
                    MotionCapture capture = new MotionCapture(document, codecRegistry);
                    cache.put(capture.getName(), capture);
                    return capture;
                } else {
                    throw new MotionCaptureNotFoundException("Cannot find motion capture with the name " + name);
                }
            }
        } catch (MotionCaptureNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deletes the MotionCapture where the given name
     *
     * @param name MotionCapture name
     */
    public void deleteMotionCapture(final String name) {
        cache.remove(name);
        MOTION_CAPTURE_COLLECTION.deleteOne(eq("name", name));
    }

    /**
     * checks if an MotionCapture with the give name exists
     *
     * @param name MotionCapture name
     * @return boolean
     */
    public boolean existsMotionCapture(final String name) {
        return MOTION_CAPTURE_COLLECTION.find(eq("name", name)).first() != null;
    }

    public List<eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture> getMotionCaptures() {
        return new ArrayList<>(cache.values());
    }

    public static class MotionCaptureScheduler implements Listener, eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler.MotionCaptureScheduler {
        private final HashMap<String, PlayerNpc> npcs;

        public MotionCaptureScheduler() {
            npcs = new HashMap<>();
            CoreSystem.getInstance().registerEvents(this);
        }

        public void addNPCs(final PlayerNpc... playerNpcs) {
            for (PlayerNpc playernpc : playerNpcs) {
                addNpc(playernpc);
            }
        }

        public void addNpc(final PlayerNpc playerNpc) {
            try {
                if (playerNpc.getMotionPlayer() != null) {
                    npcs.put(playerNpc.getData().getName(), playerNpc);
                } else {
                    throw new MotionCaptureNotDefinedException("NPC: " + playerNpc.getData().getName());
                }
            } catch (MotionCaptureNotDefinedException e) {
                e.printStackTrace();
            }
        }

        public void addNpc(final PlayerNpc playerNpc, final MotionCapture data) {
            playerNpc.playMotionCapture(data);
            npcs.put(playerNpc.getData().getName(), playerNpc);
        }

        @EventHandler
        public void on(NpcAnimationStateChangeEvent e) {
            PlayerNpc playerNpc = e.getNpc();
            if (Bukkit.getOnlinePlayers().size() != 0) {
                if (e.getState().equals(NpcAnimationStateChangeEvent.NpcAnimationState.END)) {
                    if (npcs.containsKey(playerNpc.getData().getName())) {
                        playerNpc.getMotionPlayer().restart();
                    }
                }
            }
        }

        public boolean removeNpc(final PlayerNpc npc) {
            return removeNpc(npc.getData().getName());
        }

        public boolean removeNpc(final String name) {
            if (npcs.containsKey(name)) {
                return false;
            } else {
                npcs.remove(name);
                return true;
            }
        }

        public List<PlayerNpc> getNpcs() {
            return new ArrayList<>(npcs.values());
        }
    }
}
