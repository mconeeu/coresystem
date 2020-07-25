package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import lombok.Getter;

@Getter
public enum PlayInUseAction {
    DOOR((byte) 0),
    BUTTON((byte) 1),
    BLOCK((byte) 2),
    AIR((byte) 3);

    private final byte id;

    PlayInUseAction(byte id) {
        this.id = id;
    }

    public static PlayInUseAction getByID(byte id) {
        for (PlayInUseAction action : values()) {
            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }

    public static PlayInUseAction migrate(String name) {
        for (PlayInUseAction action : values()) {
            if (action.toString().equalsIgnoreCase(name)) {
                return action;
            }
        }

        return null;
    }
}
