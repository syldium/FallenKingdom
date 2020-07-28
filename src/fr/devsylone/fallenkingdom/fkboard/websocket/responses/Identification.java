package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

public class Identification implements Response {

    private final String id;

    public Identification(String id) {
        this.id = id;
    }
    
    @Override
    public int getStatusCode()
    {
        return 950;
    }

    @Override
    public @Nonnull String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.addProperty("id", id);
        object.addProperty("senderType", "spigot-plugin");
        return object.toString();
    }
}
