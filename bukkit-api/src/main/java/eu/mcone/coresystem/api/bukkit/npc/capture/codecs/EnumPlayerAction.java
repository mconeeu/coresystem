package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import lombok.Getter;

@Getter
public enum EnumPlayerAction {

    START_SNEAKING((byte) 1),
    STOP_SNEAKING((byte) 2),
    STOP_SLEEPING((byte) 3),
    START_SPRINTING((byte) 4),
    STOP_SPRINTING((byte) 5),
    RIDING_JUMP((byte) 6),
    OPEN_INVENTORY((byte) 6);

    private final byte id;

    EnumPlayerAction(byte id) {
        this.id = id;
    }

    public static EnumPlayerAction getActionWhereID(byte id) {
        for (EnumPlayerAction action : values()) {
            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }
}
