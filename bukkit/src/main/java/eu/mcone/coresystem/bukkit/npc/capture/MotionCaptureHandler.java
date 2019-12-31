package eu.mcone.coresystem.bukkit.npc.capture;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.core.exception.MotionCaptureAlreadyExistsException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotDefinedException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotFoundException;
import lombok.Getter;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MotionCaptureHandler implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler {

    private HashMap<String, MotionCaptureData> motionCaptureDataMap;
    private MongoCollection<MotionCaptureData> motionCaptureCollection;

    @Getter
    private final MotionCaptureScheduler motionCaptureScheduler;

    public MotionCaptureHandler() {
        motionCaptureDataMap = new HashMap<>();
        motionCaptureCollection = CoreSystem.getInstance().getMongoDB().withCodecRegistry(
                fromRegistries(getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()))
        ).getCollection("motion_capture1", MotionCaptureData.class);
        motionCaptureScheduler = new MotionCaptureScheduler();
    }

    public void loadDatabase() {
        for (MotionCaptureData data : motionCaptureCollection.find()) {
            motionCaptureDataMap.put(data.getName(), data);
        }
    }

    public boolean saveMotionCapture(final MotionRecorder recorder) {
        try {
            if (motionCaptureCollection.find(eq("name", recorder.getName())).first() == null) {
                if (!recorder.isStopped()) {
                    recorder.stopRecording();
                }

                motionCaptureCollection.insertOne(new MotionCaptureData(recorder.getName(), recorder.getWorld(), recorder.getRecorded(), recorder.getRecorderName(), recorder.getPackets().size(), recorder.getPackets()));

                return true;
            } else {
                throw new MotionCaptureAlreadyExistsException();
            }
        } catch (MotionCaptureAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MotionCaptureData getMotionCapture(final String name) {
        try {
            MotionCaptureData data = motionCaptureCollection.find(eq("name", name)).first();

            if (data != null) {
                if (motionCaptureDataMap.containsKey(name)) {
                    return motionCaptureDataMap.get(name);
                } else {
                    motionCaptureDataMap.put(name, data);
                    return data;
                }
            } else {
                throw new MotionCaptureNotFoundException("Cannot found motion capture with the name " + name);
            }

        } catch (MotionCaptureNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteMotionCapture(final MotionCaptureData data) {
        deleteMotionCapture(data.getName());
    }

    public void deleteMotionCapture(final String name) {
        motionCaptureDataMap.remove(name);
        motionCaptureCollection.deleteOne(eq("name", name));
    }

    public boolean existsMotionCapture(final String name) {
        return motionCaptureCollection.find(eq("name", name)).first() != null;
    }

    public List<MotionCaptureData> getMotionCaptures() {
        return new ArrayList<>(motionCaptureDataMap.values());
    }

    public class MotionCaptureScheduler implements Listener, eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler.MotionCaptureScheduler {
        private final HashMap<String, PlayerNpc> npcs;

        public MotionCaptureScheduler() {
            npcs = new HashMap<>();
            CoreSystem.getInstance().registerEvents(this);
        }

        public void addNpcs(final PlayerNpc... playerNpcs) {
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

        public void addNpc(final PlayerNpc playerNpc, final MotionCaptureData data) {
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
