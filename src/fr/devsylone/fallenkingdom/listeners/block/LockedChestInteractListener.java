package fr.devsylone.fallenkingdom.listeners.block;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.lockedchests.LockedChest.ChestState;

public class LockedChestInteractListener implements Listener
{

	@EventHandler
	public void interact(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST) && Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getClickedBlock().getLocation()) != null)
		{
			final LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getClickedBlock().getLocation());

			if(chest.getState().equals(ChestState.UNLOCKED))
				return;

			if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			{
				e.getPlayer().sendMessage(ChatUtils.ALERT + "§4Attention§c vous ouvrez un coffre crochetable. Vous pouvez l'ouvrir car vous êtes en créatif.");
				return;
			}
			
			if(!Fk.getInstance().getGame().getState().equals(GameState.STARTED))
			{
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cLa partie est en pause/pas commencée");
				return;
			}
			
			e.setCancelled(true);
			if(chest.getUnlockDay() > Fk.getInstance().getGame().getDays())
			{
				e.getPlayer().sendMessage("§cVous ne pouvez crocheter ce coffre qu'à partir du jour " + chest.getUnlockDay());
				return;
			}

			if(chest.getUnlocker() != e.getPlayer().getName())
				chest.startUnlocking(e.getPlayer().getName());

			else
				chest.updateLastInteract();

		}

	}
}
