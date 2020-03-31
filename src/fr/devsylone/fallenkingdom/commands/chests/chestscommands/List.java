package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class List extends FkChestsCommand
{
    public List()
    {
        super("list", "", 0, "Affiche la liste des coffres à crocheter");
    }

    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        if(Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size() == 0)
            throw new FkLightException("Il n'y a aucun coffre à crocheter !");

        StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "§m-----------------" + ChatColor.BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-----------------" + System.lineSeparator());
        for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
        {
            builder.append(ChatColor.GREEN + chest.getName() + ChatColor.GRAY + " ouvrable " + ChatColor.GOLD + "jour " + chest.getUnlockDay() + ChatColor.GRAY + " en " + ChatColor.GREEN + chest.getUnlockingTime() + " s" + System.lineSeparator());
            builder.append("Coordonées : x > §c" + chest.getLocation().getBlockX() + " §7; y > §c" + chest.getLocation().getBlockY() + " §7; z > §c" + chest.getLocation().getBlockZ() + System.lineSeparator());
            builder.append(ChatColor.DARK_GREEN + "§m----------------------------------------" + System.lineSeparator());
        }
        fkp.sendMessage(builder.toString());
    }
}
