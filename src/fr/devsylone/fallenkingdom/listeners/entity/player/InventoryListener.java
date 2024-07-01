package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.PluginInventory;
import fr.devsylone.fallenkingdom.version.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener  {

    @EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		InventoryHolder holder = Environment.getInventoryHolder(event.getInventory());
		if (holder instanceof PluginInventory) {
			((PluginInventory) holder).onInventoryClick(event);
		}
	}
}
