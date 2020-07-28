package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fkpi.FkPI;

class MoveCommand extends WSCommand {

    MoveCommand() {
        super("move", true, "player", "team");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        String player = json.get("player").getAsString();
        String team = json.get("team").getAsString();
        if (fkpi.getTeamManager().getPlayerTeam(player) != null) {
            fkpi.getTeamManager().removePlayerOfHisTeam(player);
        }
        if (!team.equals("__noteam")) {
            fkpi.getTeamManager().addPlayer(player, team);
        }
        return true;
    }
}
