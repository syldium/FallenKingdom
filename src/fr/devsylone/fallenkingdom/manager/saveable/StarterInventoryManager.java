package fr.devsylone.fallenkingdom.manager.saveable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.util.Saveable;

import java.util.Arrays;

public class StarterInventoryManager implements Saveable
{
	private ItemStack[] armors = new ItemStack[4];
	private ItemStack[] inventory = new ItemStack[] {};

	private ItemStack[] lastArmors = armors;
	private ItemStack[] lastInventory = inventory;

	public void setStarterInv(PlayerInventory inv)
	{
		lastArmors = armors;
		lastInventory = inventory;

		armors = Arrays.stream(inv.getArmorContents()).map(item -> item == null ? null : item.clone()).toArray(ItemStack[]::new);
		inventory = Arrays.stream(inv.getContents()).map(item -> item == null ? null : item.clone()).toArray(ItemStack[]::new);
	}

	public boolean undo()
	{
		if(inventory == lastInventory)
			return false;

		armors = lastArmors;
		inventory = lastInventory;
		return true;
	}

	public void applyStarterInv(Player p)
	{
		p.getInventory().clear();
		p.getInventory().setArmorContents(armors);
		p.getInventory().setContents(inventory);
		p.updateInventory();
	}

	public void show(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 6 * 9, Messages.CMD_MAP_GAME_STARTER_INV_INVENTORY.getMessage());

		for(int i = 0; i < armors.length; i++)
			inv.setItem(i, armors[i]);

		for(int i = 0; i < inventory.length; i++)
			if(i < 9)
				inv.setItem(45 + i, inventory[i]);
			else if (i >= 36)
				inv.setItem(i - 36, inventory[i]);
			else
				inv.setItem(9 + i, inventory[i]);

		p.openInventory(inv);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(config.contains("armors"))
			for(String key : config.getConfigurationSection("armors").getKeys(false))
				armors[Integer.parseInt(key)] = config.getItemStack("armors." + key);

		if(config.contains("inventory"))
		{
			Integer[] keys = config.getConfigurationSection("inventory").getKeys(false).stream().map(Integer::parseInt).toArray(Integer[]::new);
			inventory = new ItemStack[keys[keys.length-1]+1];
			for (int key : keys)
				inventory[key] = config.getItemStack("inventory." + key);
		}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(int i = 0; i < armors.length; i++)
			if(armors[i] != null)
				config.set("armors." + i, armors[i]);

		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] != null)
				config.set("inventory." + i, inventory[i]);
	}
}
