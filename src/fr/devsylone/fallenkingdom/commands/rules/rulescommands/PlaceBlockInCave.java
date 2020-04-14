package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class PlaceBlockInCave extends FkRuleCommand
{
  public PlaceBlockInCave()
  {
    super("placeBlockInCave", "<true|false|info> [blocks]", 1, Messages.CMD_MAP_RULES_PLACE_BLOCK_IN_CAVE);
  }
  

  public void execute(Player sender, FkPlayer fkp, String[] args)
  {
    fr.devsylone.fkpi.rules.PlaceBlockInCave rule = Fk.getInstance().getFkPI().getRulesManager().getRule(Rule.PLACE_BLOCK_IN_CAVE);
    
    if (args[0].equalsIgnoreCase("info"))
    {
      fkp.sendMessage("§b§m-------");
      fkp.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO.getMessage().replace("%blocks%", String.valueOf(rule.getMinimumBlocks())));
      fkp.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_MODIFIABLE);
      fkp.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_ENEMY);
      fkp.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_TNT.getMessage().replace("%cap%", String.valueOf(FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP))));
      fkp.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_ALLOWED_BLOCKS);
      fkp.sendMessage(FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS).format());
      fkp.sendMessage("§b§m-------");
    }
    else
    {
      if ((!args[0].equalsIgnoreCase("true")) && (!args[0].equalsIgnoreCase("false"))) {
        throw new FkLightException(Messages.CMD_ERROR_BOOL_FORMAT);
      }
      
      rule.setActive(Boolean.parseBoolean(args[0]));

      if (args.length > 1) {
        int min = assertPositiveNumber(args[1],false, Messages.CMD_ERROR_POSITIVE_INT);
        rule.setMinimumBlocks(min);
      }
      if (rule.isActive())
        broadcast(Messages.CMD_RULES_BLOCK_CAVE_ACTIVE.getMessage());
      else
        broadcast(Messages.CMD_RULES_BLOCK_CAVE_INACTIVE.getMessage());
      broadcast(Messages.CMD_RULES_BLOCK_CAVE_CONSECUTIVE.getMessage().replace("%blocks%", String.valueOf(rule.getMinimumBlocks())));
      broadcast(Messages.CMD_RULES_BLOCK_CAVE_MORE_INFO.getMessage());
    }
  }
}
