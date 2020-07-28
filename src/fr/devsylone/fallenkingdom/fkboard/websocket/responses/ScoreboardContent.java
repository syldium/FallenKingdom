package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;

public class ScoreboardContent implements Response {

    private final Map<String, String> placeholders;
    private final List<String> lines;

    public ScoreboardContent(Map<String, String> placeholders, List<String> lines) {
        this.placeholders = placeholders;
        this.lines = lines;
    }

    public ScoreboardContent(PlaceHolder[] placeholders, List<String> lines) {
        Map<String, String> placeholdersMap = new HashMap<>();
        for (PlaceHolder placeholder : placeholders) {
            placeholdersMap.put(placeholder.getShortestKey(), placeholder.getDescription());
        }
        this.placeholders = placeholdersMap;
        this.lines = lines;
    }

    @Override
    public int getStatusCode() {
        return 1004;
    }

    @Override
    public @Nonnull String toJSON() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.add("placeholders", gson.toJsonTree(placeholders));
        object.add("lines", gson.toJsonTree(lines));
        return object.toString();
    }
}
