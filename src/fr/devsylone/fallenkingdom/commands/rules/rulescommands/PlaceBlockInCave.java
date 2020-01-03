package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.util.BlockDescription;

public class PlaceBlockInCave extends fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand
{
  public PlaceBlockInCave()
  {
    super("placeBlockInCave", "<true|false|info> [blocks]", 1, " À true il est possible de poser n'importe quel bloc lorsque que l'on est en caverne.");
  }
  

  public void execute(Player sender, FkPlayer fkp, String[] args)
  {
    fr.devsylone.fkpi.rules.PlaceBlockInCave rule = (fr.devsylone.fkpi.rules.PlaceBlockInCave)Fk.getInstance().getFkPI().getRulesManager().getRuleByName("PlaceBlockInCave");
    
    if (args[0].equalsIgnoreCase("info"))
    {
      fkp.sendMessage("§b§m-------");
      fkp.sendMessage("§aUn bloc est considéré posé en caverne lorsqu'au moins §2" + rule.getMinimumBlocks() + "§a blocs de roche consécutifs se trouvent au dessus de lui.");
      fkp.sendMessage("§aCette valeur est modifiable grâce au paramètre §e[blocks] §ade la commande §e/fk rule PlaceBlockInCave <true|false> [blocks]");
      fkp.sendMessage("§aSi vous vous trouvez sous une base ennemie, vous ne §cpourrez pas poser de bloc en dehors de la liste de blocs autorisés");
      fkp.sendMessage("§aIl est impossible de poser de la §cTNT §aavant le jour §c" + Fk.getInstance().getFkPI().getRulesManager().getRuleByName("tntcap").getValue() + "§a, en caverne comme à la surface.");
      fkp.sendMessage("§aPour rappel, voici la liste des blocs autorisés à être posé en dehors de votre base à la surface et en caverne : ");
      for (BlockDescription b : ((AllowedBlocks)Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks")).getValue())
        fkp.sendMessage("§d- " + b.toString());
      fkp.sendMessage("§b§m-------");
    }
    else
    {
      if ((!args[0].equalsIgnoreCase("true")) && (!args[0].equalsIgnoreCase("false"))) {
        throw new FkLightException(args[0] + " n'est pas un booléen valide ! (true - false) ");
      }
      
      rule.setValue(Boolean.valueOf(Boolean.parseBoolean(args[0])));
      
      if (args.length > 1) {
        try
        {
          rule.setMinimumBlocks(Integer.parseInt(args[1]));
        }
        catch (NumberFormatException e) {
          fkp.sendMessage("§cVotre nombre n'est pas valide, le nombre de blocs de roches minimum reste à §4" + rule.getMinimumBlocks());
        }
      }
      broadcast("§4§luniquement en caverne§6, il est maintenant", Boolean.valueOf(args[0]).booleanValue() ? "possible" : "impossible", "de poser d'autres blocs que ceux figurant dans la liste des blocs autorisés !");
      broadcast("§6La limite de blocs pour être considéré en caverne est de : §4" + rule.getMinimumBlocks() + " §6blocs consécutifs");
      broadcast("§e/fk rules PlaceBlockInCave info§a pour plus d'informations.");
    }
  }
}
