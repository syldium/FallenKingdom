package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener
{
	@EventHandler
	public void click(InventoryClickEvent e)
	{
		if(e.getView().getTitle().equals("§bInventaire de départ"))
			e.setCancelled(true);
	}
}
