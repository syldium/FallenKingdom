package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class List extends FkChestsCommand
{
    public List()
    {
        super("list", "", 0, Messages.CMD_MAP_CHEST_LIST.getMessage());
    }

    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        if(Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size() == 0)
            throw new FkLightException("Il n'y a aucun coffre à crocheter !");

        StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "§m-----------------" + ChatColor.BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-----------------" + System.lineSeparator());
        for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
        {
            builder.append(ChatColor.GREEN).append(chest.getName()).append(ChatColor.GRAY).append(" ouvrable ").append(ChatColor.GOLD).append("jour ").append(chest.getUnlockDay()).append(ChatColor.GRAY).append(" en ").append(ChatColor.GREEN).append(chest.getUnlockingTime()).append(" s").append(System.lineSeparator());
            builder.append("Coordonnées : x > §c").append(chest.getLocation().getBlockX()).append(" §7; y > §c").append(chest.getLocation().getBlockY()).append(" §7; z > §c").append(chest.getLocation().getBlockZ()).append(System.lineSeparator());
            builder.append(ChatColor.DARK_GREEN + "§m----------------------------------------").append(System.lineSeparator());
        }
        fkp.sendMessage(builder.toString());
    }
}
