package fr.devsylone.fallenkingdom.pause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.devsylone.fkpi.util.Saveable;

import static fr.devsylone.fallenkingdom.utils.ConfigHelper.enumValueOf;
import static fr.devsylone.fallenkingdom.utils.ConfigHelper.getLocation;
import static fr.devsylone.fallenkingdom.utils.ConfigHelper.setLocation;

public class PausedPlayer implements Saveable
{
	private String player;
	private ItemStack[] inv;
	private double health;
	private int food;
	private float saturation;
	private float xp;
	private int oxygen;
	private GameMode gm;
	private Location loc;
	private List<PotionEffect> effects;

	public PausedPlayer(ConfigurationSection config)
	{
		load(config);
	}

	public PausedPlayer(Player p)
	{
		this(p.getName(), p.getInventory(), p.getHealth(), p.getFoodLevel(), p.getSaturation(), p.getExp(), p.getRemainingAir(), p.getGameMode(), p.getLocation(), p.getActivePotionEffects());
	}

	public PausedPlayer(String pl, PlayerInventory invArg, double h, int f, float s, float xp, int oxygen, GameMode gm, Location loc, Collection<PotionEffect> e)
	{
		player = pl;
		health = h;
		food = f;
		saturation = s;
		this.xp = xp;
		this.oxygen = oxygen;
		this.gm = gm;
		this.loc = loc;

		effects = new ArrayList<>();
		effects.addAll(e);

		inv = new ItemStack[40];

		inv[0] = cloneIfNotNull(invArg.getHelmet());
		inv[1] = cloneIfNotNull(invArg.getChestplate());
		inv[2] = cloneIfNotNull(invArg.getLeggings());
		inv[3] = cloneIfNotNull(invArg.getBoots());

		for(int i = 0; i < 36; i++)
			inv[i + 4] = cloneIfNotNull(invArg.getItem(i));

	}

	public boolean tryRestore()
	{
		Player p = Bukkit.getPlayer(player);
		if(p == null)
			return false;

		else
		{
			p.getInventory().setHelmet(inv[0]);
			p.getInventory().setChestplate(inv[1]);
			p.getInventory().setLeggings(inv[2]);
			p.getInventory().setBoots(inv[3]);

			for(int i = 0; i < 36; i++)
				p.getInventory().setItem(i, inv[i + 4]);
			p.updateInventory();

			for(PotionEffect pe : p.getActivePotionEffects())
				p.removePotionEffect(pe.getType());
			p.addPotionEffects(effects);

			p.setHealth(health);
			p.setFoodLevel(food);
			p.setSaturation(saturation);
			p.setExp(xp);
			p.setRemainingAir(oxygen);
			p.setGameMode(gm);
			p.teleport(loc);

			return true;
		}
	}

	public String getPlayer()
	{
		return player;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PausedPlayer that = (PausedPlayer) o;
		return player.equals(that.player);
	}

	@Override
	public int hashCode() {
		return player.hashCode();
	}

	private ItemStack cloneIfNotNull(ItemStack item)
	{
		return item == null ? null : item.clone();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		player = config.getString("player");
		health = config.getDouble("health");
		food = config.getInt("player");
		saturation = (float) config.getDouble("player");
		xp = (float) config.getDouble("player");
		oxygen = config.getInt("oxygen", 300);
		gm = enumValueOf(GameMode.class, config.getString("gm"), GameMode.SURVIVAL);

		loc = getLocation(config.getConfigurationSection("loc"));

		effects = new ArrayList<>();

		if(config.isConfigurationSection("effects"))
			for(String key : config.getConfigurationSection("effects").getKeys(false))
				effects.add(new PotionEffect(PotionEffectType.getByName(key), config.getInt("effects." + key + ".duration"), config.getInt("effects." + key + ".amplifier")));

		inv = new ItemStack[40];
		inv[0] = config.getItemStack("inv.armor.helmet");
		inv[1] = config.getItemStack("inv.armor.chestplate");
		inv[2] = config.getItemStack("inv.armor.leggings");
		inv[3] = config.getItemStack("inv.armor.boots");

		if(config.isConfigurationSection("inv"))
			for(String key : config.getConfigurationSection("inv").getKeys(false))
				if(!key.equals("armor"))
					inv[Integer.parseInt(key) + 4] = config.getItemStack("inv." + key);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("player", player);
		config.set("health", health);
		config.set("food", food);
		config.set("saturation", (double) saturation);
		config.set("xp", (double) xp);
		config.set("oxygen", oxygen);
		config.set("gm", gm.name());

		setLocation(config.createSection("loc"), loc);

		config.createSection("effects");

		for(PotionEffect pe : effects)
		{
			config.set("effects." + pe.getType().getName() + ".amplifier", pe.getAmplifier());
			config.set("effects." + pe.getType().getName() + ".duration", pe.getDuration());
		}

		config.set("inv.armor.helmet", inv[0]);
		config.set("inv.armor.chestplate", inv[1]);
		config.set("inv.armor.leggings", inv[2]);
		config.set("inv.armor.boots", inv[3]);

		for(int i = 0; i < 36; i++)
			if(inv[i + 4] != null)
				config.set("inv." + i, inv[i + 4]);
	}

}
