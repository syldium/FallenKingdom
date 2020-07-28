package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import org.bukkit.ChatColor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.FkPI;

class UpdateScoreboardCommand extends WSCommand {

    UpdateScoreboardCommand() {
        super("update scoreboard", true, "lines");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        if (!json.get("lines").isJsonArray()) {
            return false;
        }
        Fk.getInstance().getScoreboardManager().getSidebar().clear();
        for (JsonElement lineElement : json.get("lines").getAsJsonArray()) {
            String line = lineElement.getAsString();
            if (line.length() < 5) {
                line += ScoreboardManager.randomFakeEmpty();
            }
            if ((Version.VersionType.V1_13.isHigherOrEqual() && line.length() <= 64) || line.length() <= 32) {
                Fk.getInstance().getScoreboardManager().getSidebar().add(line);
            } else {
                Fk.getInstance().getScoreboardManager().getSidebar().add(ChatColor.ITALIC + "invalid");
            }
        }
        return true;
    }
}
