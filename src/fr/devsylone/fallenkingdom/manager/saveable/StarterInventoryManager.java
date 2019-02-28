package fr.devsylone.fallenkingdom.manager.saveable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.devsylone.fkpi.util.Saveable;

public class StarterInventoryManager implements Saveable
{
	private ItemStack[] armors;
	private ItemStack[] inventory;

	private ItemStack[] lastArmors;
	private ItemStack[] lastInventory;

	public StarterInventoryManager()
	{
		armors = new ItemStack[4];
		inventory = new ItemStack[36];

		lastArmors = armors;
		lastInventory = inventory;
	}

	public void setStarterInv(PlayerInventory inv)
	{
		lastArmors = armors;
		lastInventory = inventory;

		armors = inv.getArmorContents();
		inventory = inv.getContents();
	}

	public boolean undo()
	{
		if(armors == lastArmors)
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
		Inventory inv = Bukkit.createInventory(null, 6 * 9, "§bInventaire de départ");

		for(int i = 0; i < armors.length; i++)
			inv.setItem(i, armors[i]);

		for(int i = 0; i < inventory.length; i++)
			if(i < 9)
				inv.setItem(45 + i, inventory[i]);
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
			for(String key : config.getConfigurationSection("inventory").getKeys(false))
				inventory[Integer.parseInt(key)] = config.getItemStack("inventory." + key);
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
