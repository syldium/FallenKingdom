package fr.devsylone.fallenkingdom.fkboard.websocket.commands;


import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fkpi.FkPI;

public class CommandsManager {
    private final List<WSCommand> commands;

    public CommandsManager() {
        this.commands = ImmutableList.<WSCommand>builder()
                .add(new ChangeTeamNameCommand())
                .add(new DeleteTeamCommand())
                .add(new EditRuleCommand())
                .add(new FetchScoreboardCommand())
                .add(new InsertTeamCommand())
                .add(new ListRulesCommand())
                .add(new MoveCommand())
                .add(new UpdateScoreboardCommand())
                .build();
    }

    public boolean executeCommand(Fk plugin, FkPI fkpi,  FkBoardWebSocket webSocket, String action, JsonObject json) {
        WSCommand cmd = commands.stream()
                .filter(node -> node.path.equalsIgnoreCase(action))
                .findFirst()
                .orElse(null);

        if (cmd == null || !cmd.hasRequiredJsonKeys(json)) {
            return false;
        }
        boolean result = cmd.execute(plugin, fkpi, webSocket, json);
        if (result && cmd.needScoreboardReload) {
            webSocket.runSync(() ->  Fk.getInstance().getScoreboardManager().recreateAllScoreboards());
        }
        return result;
    }
}
