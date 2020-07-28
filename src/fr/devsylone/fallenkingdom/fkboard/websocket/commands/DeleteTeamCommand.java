package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fkpi.FkPI;

class DeleteTeamCommand extends WSCommand {

    DeleteTeamCommand() {
        super("delete team", true, "team");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        fkpi.getTeamManager().removeTeam(json.get("team").getAsString());
        return true;
    }
}
