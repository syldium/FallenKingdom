package fr.devsylone.fallenkingdom.version.potion;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public interface BrewerAccess {

    BrewerAccess INSTANCE = Provider.createInstance();

    ItemStack mix(World world, ItemStack input, ItemStack ingredient);
}
