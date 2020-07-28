package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.RulesList;
import fr.devsylone.fkpi.FkPI;

class ListRulesCommand extends WSCommand {

    ListRulesCommand() {
        super("list rules", false);
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        webSocket.sendWithEncryption(new RulesList(fkpi.getRulesManager().getRulesList()).toJSON());
        return true;
    }
}
