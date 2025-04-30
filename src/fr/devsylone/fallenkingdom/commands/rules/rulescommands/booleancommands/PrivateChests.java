package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class PrivateChests extends FkBooleanRuleCommand {

    public PrivateChests() {
        super("privateChests", Messages.PLAYER_BLOCK_PRIVATE, Rule.PRIVATE_CHESTS);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastOnOff(newValue, Messages.CMD_RULES_PRIVATE_CHESTS);
    }
}
