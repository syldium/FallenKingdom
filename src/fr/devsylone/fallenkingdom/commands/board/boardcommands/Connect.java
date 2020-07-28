package fr.devsylone.fallenkingdom.commands.board.boardcommands;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.Argument;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;

public class Connect extends FkCommand {
    public Connect() {
        super("connect", Argument.list(Argument.create("id", true, "Id indiquée sur la page web")), Messages.CMD_MAP_BOARD_CONNECT, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        if (plugin.getOptionalFkBoardWebSocket().isPresent()) {
            sender.sendMessage(Messages.CMD_ERROR_BOARD_ALREADY_CONNECTED.getMessage());
            return CommandResult.STATE_ERROR;
        }
        plugin.createNewFkBoardWebSocket(args.get(0), () -> sender.sendMessage("§cId invalide"));
        return CommandResult.SUCCESS;
    }
}
