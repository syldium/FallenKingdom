package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class BucketAssault extends FkBooleanRuleCommand {

    public BucketAssault() {
        super("bucketAssault", Messages.CMD_MAP_RULES_BUCKET_ASSAULT, Rule.BUCKET_ASSAULT);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastPossibleImpossible(newValue, Messages.CMD_RULES_BUCKET_ASSAULT);
    }
}
