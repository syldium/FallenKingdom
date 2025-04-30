package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class BlastProofBase extends FkBooleanRuleCommand {

    public BlastProofBase() {
        super("blastProofBase", Messages.CMD_MAP_RULES_BLAST_PROOF_BASE, Rule.BLAST_PROOF_BASE);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastPossibleImpossible(newValue, Messages.CMD_RULES_BLAST_PROOF_BASE);
    }
}
