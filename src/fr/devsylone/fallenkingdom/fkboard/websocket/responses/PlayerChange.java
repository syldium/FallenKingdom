package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import fr.devsylone.fkpi.api.ITeam;

public class PlayerChange implements Response {

    private final String player;
    private final String team;
    private final boolean logged;

    public PlayerChange(@Nonnull String player, @Nullable ITeam team, boolean logged) {
        this.player = player;
        this.team = team == null ? "__noteam" : team.getName();
        this.logged = logged;
    }

    @Override
    public int getStatusCode() {
        return 1001;
    }

    @Override
    public @Nonnull String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.addProperty("player", player);
        object.addProperty("team", team);
        object.addProperty("logged", logged);
        return object.toString();
    }
}
