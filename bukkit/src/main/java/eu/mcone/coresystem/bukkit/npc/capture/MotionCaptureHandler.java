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
import eu.mcone.coresystem.api.bukkit.npc.capture.codecs.PlayInUseBlockCodec;
import eu.mcone.coresystem.api.bukkit.npc.capture.codecs.PlayInUseItemCodec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.core.exception.MotionCaptureAlreadyExistsException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotDefinedException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class MotionCaptureHandler implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler {

    private static final MongoCollection<Document> MOTION_CAPTURE_COLLECTION = ((CoreModuleCoreSystem) BukkitCoreSystem.getInstance()).getMongoDB(Database.SYSTEM).getCollection("motion_capture");

    private final HashMap<String, MotionCapture> cache;

    @Getter
    private final MotionCaptureScheduler motionCaptureScheduler;
    @Getter
    private final CodecRegistry codecRegistry;

    public MotionCaptureHandler() {
        cache = new HashMap<>();
        motionCaptureScheduler = new MotionCaptureScheduler();
        codecRegistry = CoreSystem.getInstance().createCodecRegistry(false);

        codecRegistry.registerCodec((byte) 1, eu.mcone.coresystem.api.bukkit.npc.capture.codecs.PlayerMoveEventCodec.class, PlayerMoveEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 2, PlayInUseBlockCodec.class, PlayerInteractEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 3, PlayInUseItemCodec.class, PlayerInteractEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 4, eu.mcone.coresystem.api.bukkit.npc.capture.codecs.ItemSwitchEventCodec.class, PlayerItemHeldEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 5, eu.mcone.coresystem.api.bukkit.npc.capture.codecs.PlayInEntityActionCodec.class, PacketPlayInEntityAction.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 6, eu.mcone.coresystem.api.bukkit.npc.capture.codecs.PlayOutAnimationCodec.class, PacketPlayOutAnimation.class, (byte) 2, PlayerNpc.class);

        loadDatabase();
    }

    /**
     * Loads all motion captures from the database and stores it locally
     */
    public void loadDatabase() {
        for (Document document : MOTION_CAPTURE_COLLECTION.find()) {
            cache.put(document.getString("name"), new MotionCapture(this, document));
        }
    }

    public boolean migrateChunk(String name, byte[] codecs) {
        if (MOTION_CAPTURE_COLLECTION.find(eq("name", name)).first() != null) {
            MOTION_CAPTURE_COLLECTION.updateOne(eq("name", name), set("chunk", codecs));
        }

        return false;
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
    public MotionCapture getMotionCapture(final String name) throws MotionCaptureNotDefinedException {
        if (cache.containsKey(name)) {
            return cache.get(name);
        } else {
            Document document = MOTION_CAPTURE_COLLECTION.find(eq("name", name)).first();

            if (document != null) {
                MotionCapture capture = new MotionCapture(this, document);
                cache.put(capture.getName(), capture);
                return capture;
            } else {
                throw new MotionCaptureNotDefinedException("Cannot find motion capture with the name " + name);
            }
        }
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
                if (playerNpc.getCapturePlayer() != null) {
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
                        playerNpc.getCapturePlayer().restart();
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
