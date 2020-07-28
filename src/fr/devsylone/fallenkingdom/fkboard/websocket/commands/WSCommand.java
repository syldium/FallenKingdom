package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fkpi.FkPI;

public abstract class WSCommand {

    protected final String path;
    protected final boolean needScoreboardReload;
    protected final String[] requiredJsonKeys;

    WSCommand(String path, boolean needScoreboardReload, String... requiredJsonKeys) {
        this.path = path;
        this.needScoreboardReload = needScoreboardReload;
        this.requiredJsonKeys = requiredJsonKeys;
    }

    public boolean hasRequiredJsonKeys(JsonObject json) {
        for (String key : requiredJsonKeys) {
            if (!json.has(key)) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json);
}
