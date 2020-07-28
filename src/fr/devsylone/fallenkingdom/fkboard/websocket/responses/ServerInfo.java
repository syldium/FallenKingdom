package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;

public class ServerInfo implements Response {

    private final String pluginVersion;
    private final String serverVersion;

    public ServerInfo(@Nonnull Fk plugin) {
        pluginVersion = plugin.getDescription().getVersion();
        serverVersion = plugin.getServer().getVersion().replaceAll("[\\w-]+ \\(MC: ([\\d.]+)\\)", "$1");
    }

    @Override
    public int getStatusCode() {
        return 999;
    }

    @Override
    public @Nonnull String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.addProperty("pluginVersion", pluginVersion);
        object.addProperty("serverVersion", serverVersion);
        return object.toString();
    }
}
