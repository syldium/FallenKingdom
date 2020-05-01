package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class EnderpearlAssault extends FkBooleanRuleCommand
{
    public EnderpearlAssault()
    {
        super("enderpearlAssault", Messages.CMD_MAP_RULES_ENDERPEARL, Rule.ENDERPEARL_ASSAULT);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastPossibleImpossible(newValue, Messages.CMD_RULES_ENDERPEARL_ASSAULT);
    }
}
