package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import java.util.List;

// Cette classe permet de retirer le bloc static de l'originale, comme sa partie static fait appel à des éléments bloquants les tests.
public class DisabledPotions extends FkPlayerCommand {

    public DisabledPotions() {
        super("disabledPotions", Messages.CMD_MAP_RULES_DISABLED_POTIONS, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        return CommandResult.SUCCESS;
    }
}
