package fr.devsylone.fallenkingdom.commands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help
  extends FkCommand
{
  public Help()
  {
    super("help", "", 0, "Donne la liste des commandes.");
  }
  

  public void execute(Player sender, FkPlayer fkp, String[] args)
  {
    Fk.getInstance().getCommandManager().sendHelp(sender);
  }
}
