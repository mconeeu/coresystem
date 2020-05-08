package eu.mcone.coresystem.api.core.labymod;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LabyModMiddleClickAction {

    public enum ActionType {
        NONE,
        CLIPBOARD,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_BROWSER
    }

    private final String displayName;
    private final ActionType type;
    private final String value;

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("displayName", displayName);
        obj.addProperty("type", type.name());
        obj.addProperty("value", value);

        return obj;
    }

}
