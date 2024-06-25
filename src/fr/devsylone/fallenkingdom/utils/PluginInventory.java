package fr.devsylone.fallenkingdom.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface PluginInventory extends InventoryHolder {

    void onInventoryClick(@NotNull InventoryClickEvent event);
}
