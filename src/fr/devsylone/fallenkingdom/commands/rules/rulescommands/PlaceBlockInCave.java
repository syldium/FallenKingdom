package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

import java.util.List;

public class PlaceBlockInCave extends FkCommand
{
  public PlaceBlockInCave()
  {
    super("placeBlockInCave", "<true|false|info> [i1:blocks]", Messages.CMD_MAP_RULES_PLACE_BLOCK_IN_CAVE, CommandPermission.PLAYER);
  }

  @Override
  public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
    fr.devsylone.fkpi.rules.PlaceBlockInCave rule = Fk.getInstance().getFkPI().getRulesManager().getRule(Rule.PLACE_BLOCK_IN_CAVE);

    if (args.get(0).equalsIgnoreCase("info"))
    {
      sender.sendMessage("§b§m-------");
      sender.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO.getMessage().replace("%blocks%", String.valueOf(rule.getMinimumBlocks())));
      sender.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_MODIFIABLE.getMessage());
      sender.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_ENEMY.getMessage());
      sender.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_TNT.getMessage().replace("%cap%", String.valueOf(FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP))));
      sender.sendMessage(Messages.CMD_RULES_BLOCK_CAVE_INFO_ALLOWED_BLOCKS.getMessage());
      sender.sendMessage(FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS).format());
      sender.sendMessage("§b§m-------");
    }
    else
    {
      if ((!args.get(0).equalsIgnoreCase("true")) && (!args.get(0).equalsIgnoreCase("false"))) {
        throw new FkLightException(Messages.CMD_ERROR_BOOL_FORMAT);
      }

      rule.setActive(Boolean.parseBoolean(args.get(0)));

      if (args.size() > 1) {
        int min = ArgumentParser.parsePositiveInt(args.get(1), false, Messages.CMD_ERROR_POSITIVE_INT);
        rule.setMinimumBlocks(min);
      }
      if (rule.isActive())
        broadcast(Messages.CMD_RULES_BLOCK_CAVE_ACTIVE.getMessage());
      else
        broadcast(Messages.CMD_RULES_BLOCK_CAVE_INACTIVE.getMessage());
      broadcast(Messages.CMD_RULES_BLOCK_CAVE_CONSECUTIVE.getMessage().replace("%blocks%", String.valueOf(rule.getMinimumBlocks())));
      broadcast(Messages.CMD_RULES_BLOCK_CAVE_MORE_INFO.getMessage());
    }
    return CommandResult.SUCCESS;
  }
}
