package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.ScoreboardContent;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fkpi.FkPI;

class FetchScoreboardCommand extends WSCommand {

    FetchScoreboardCommand() {
        super("fetch scoreboard", false);
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        ScoreboardContent response = new ScoreboardContent(PlaceHolder.values(), Fk.getInstance().getScoreboardManager().getSidebar());
        webSocket.sendWithEncryption(response.toJSON());
        return true;
    }
}
