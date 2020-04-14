package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener
{
	@EventHandler
	public void click(InventoryClickEvent e)
	{
		if(e.getView().getTitle().contains(Messages.INVENTORY_STARTER_TITLE.getMessage()))
			e.setCancelled(true);
	}
}
