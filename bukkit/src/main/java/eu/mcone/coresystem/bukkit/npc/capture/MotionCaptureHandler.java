package eu.mcone.coresystem.bukkit.npc.capture;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;
import eu.mcone.coresystem.api.core.exception.MotionCaptureAlreadyExistsException;
import eu.mcone.coresystem.api.core.exception.MotionCaptureNotFoundException;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import org.bson.Document;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MotionCaptureHandler implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureHandler {

    private HashMap<String, MotionCaptureData> motionCaptureDataMap;
    private MongoCollection<Document> motionCaptureCollection;

    public MotionCaptureHandler() {
        motionCaptureDataMap = new HashMap<>();
        motionCaptureCollection = CoreSystem.getInstance().getMongoDB().getCollection("motion_capture");
    }

    public void loadDatabase() {
        for (Document document : motionCaptureCollection.find()) {
            motionCaptureDataMap.put(document.getString("name"), new MotionCaptureData(document.getString("name"), document.getString("world"), document.getLong("recorded"), document.getString("creator"), document.getInteger("length"), (Map<Integer, List<PacketWrapper>>) GenericUtils.deserialize(document.get("packets", Binary.class).getData())));
            CoreSystem.getInstance().sendConsoleMessage("§aLoad motion capture " + document.getString("name"));
        }
    }

    public boolean saveMotionCapture(final String name, final MotionRecorder recorder) {
        try {
            if (!motionCaptureDataMap.containsKey(name)) {
                if (motionCaptureCollection.find(eq("name", name)).first() == null) {
                    if (!recorder.isStopped()) {
                        recorder.stopRecording();
                    }

                    MotionCaptureData data = new MotionCaptureData(name, recorder.getWorld(), recorder.getRecorded(), recorder.getRecorderName(), recorder.getTicks(), recorder.getPackets());
                    motionCaptureDataMap.put(name, data);
                    motionCaptureCollection.insertOne(data.createBsonDocument());
                    return true;
                } else {
                    throw new MotionCaptureAlreadyExistsException("The motion capture " + name + " already exists!");
                }
            } else {
                throw new MotionCaptureAlreadyExistsException("The motion capture " + name + " already exists!");
            }
        } catch (MotionCaptureAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MotionCaptureData getMotionCapture(final String name) {
        try {
            if (motionCaptureDataMap.containsKey(name)) {
                return motionCaptureDataMap.get(name);
            } else {
                Document document = motionCaptureCollection.find(eq("name", name)).first();

                if (document != null) {
                    MotionCaptureData motionCaptureData = new MotionCaptureData(document.getString("name"), document.getString("world"), document.getLong("recorded"), document.getString("creator"), document.getInteger("length"), (Map<Integer, List<PacketWrapper>>) GenericUtils.deserialize(document.get("packets", Binary.class).getData()));
                    motionCaptureDataMap.put(name, motionCaptureData);
                    return motionCaptureData;
                } else {
                    throw new MotionCaptureNotFoundException("Cannot found motion capture with the name " + name);
                }
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

    public List<MotionCaptureData> getMotionCaptures() {
        return new ArrayList<>(motionCaptureDataMap.values());
    }
}
