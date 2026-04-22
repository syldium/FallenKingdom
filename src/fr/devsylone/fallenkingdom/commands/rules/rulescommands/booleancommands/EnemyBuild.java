package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

public class EnemyBuild extends FkBooleanRuleCommand {

    public EnemyBuild() {
        super("enemyBuild", Messages.CMD_MAP_RULES_ENEMY_BUILD, Rule.ENEMY_BUILD);
    }

    @Override
    protected void sendMessage(boolean newValue) {
        broadcastOnOff(newValue, Messages.CMD_RULES_ENEMY_BUILD);
    }
}
