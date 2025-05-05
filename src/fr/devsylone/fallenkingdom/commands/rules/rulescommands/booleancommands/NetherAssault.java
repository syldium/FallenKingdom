package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class NetherAssault extends FkBooleanRuleCommand {

    public NetherAssault() {
        super("netherAssault", Messages.CMD_MAP_RULES_NETHER_ASSAULT, Rule.NETHER_ASSAULT);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastPossibleImpossible(newValue, Messages.CMD_RULES_NETHER_ASSAULT);
    }
}
