package fr.devsylone.fallenkingdom.listeners.block;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.lockedchests.LockedChest.ChestState;

public class LockedChestInteractListener implements Listener
{

	@EventHandler
	public void interact(PlayerInteractEvent e)
	{
		if (!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
			return;
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST) && Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getClickedBlock().getLocation()) != null)
		{
			final LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getClickedBlock().getLocation());

			if(chest.getState().equals(ChestState.UNLOCKED))
				return;

			if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			{
				e.getPlayer().sendMessage(ChatUtils.ALERT + Messages.PLAYER_OPEN_LOCKED_CHEST_CREATIVE);
				return;
			}
			
			e.setCancelled(true);
			if(chest.getUnlockDay() > Fk.getInstance().getGame().getDay())
			{
				e.getPlayer().sendMessage(Messages.PLAYER_LOCKED_CHEST_TOO_EARLY.getMessage().replace("%day%", String.valueOf(chest.getUnlockDay())));
				return;
			}

			// Si le joueur vise la partie supérieure du coffre, l'armorstand va se placer entre lui et le coffre, et le client n'essayera plus de l'ouvrir, ce qui n'est pas voulu.
			chest.setYFixByBlockFace(e.getBlockFace());
			if(chest.getUnlocker() != e.getPlayer().getName())
				chest.startUnlocking(e.getPlayer());
			else
				chest.updateLastInteract();
		}

	}
}
