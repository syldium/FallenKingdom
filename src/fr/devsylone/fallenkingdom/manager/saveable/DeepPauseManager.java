package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fkpi.util.Saveable;

public class DeepPauseManager implements Saveable
{
	private List<Entity> noAI;
	private List<Entity> unDespawnable;

	public DeepPauseManager()
	{
		noAI = new ArrayList<Entity>();
		unDespawnable = new ArrayList<Entity>();
		try
		{
			NMSUtils.register("net.minecraft.server._version_.Entity");
			NMSUtils.register("net.minecraft.server._version_.NBTTagCompound");
			NMSUtils.register("org.bukkit.craftbukkit._version_.entity.CraftEntity");

			NMSUtils.register("org.bukkit.craftbukkit._version_.entity.CraftItem");
			NMSUtils.register("net.minecraft.server._version_.EntityItem");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void protectDespawnItems()
	{
		if((Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeepPause").getValue())
		{
			for(World w : Bukkit.getWorlds())
			{
				for(org.bukkit.entity.Entity ent : w.getEntities())
				{
					if(ent.getType().equals(EntityType.DROPPED_ITEM))
					{
						try
						{
							Object entityItem = NMSUtils.getClass("EntityItem").cast(NMSUtils.getClass("CraftItem").getMethod("getHandle").invoke(NMSUtils.getClass("CraftItem").cast((Item) ent)));
							PacketUtils.setField("age", -100000, entityItem);
							unDespawnable.add(ent);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public void unprotectItems()
	{
		for(Entity item : unDespawnable)
		{
			try
			{
				Object entityItem = NMSUtils.getClass("EntityItem").cast(NMSUtils.getClass("CraftItem").getMethod("getHandle").invoke(NMSUtils.getClass("CraftItem").cast((Item) item)));
				PacketUtils.setField("age", 0, entityItem);
			}catch(Exception e)
			{
				//dommage lol
			}
		}
		unDespawnable.clear();
	}

	public void removeAIs() throws ReflectiveOperationException
	{
		if((Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeepPause").getValue())
		{
			for(World w : Bukkit.getWorlds())
			{
				for(org.bukkit.entity.Entity ent : w.getEntities())
				{
					if(!(ent instanceof Player))
					{
						if(Bukkit.getBukkitVersion().contains("1.8"))
						{
							Object craftEntity = NMSUtils.getClass("CraftEntity").cast(ent);
							Object nmsEntity = NMSUtils.getClass("CraftEntity").getMethod("getHandle").invoke(craftEntity);

							Object tag = NMSUtils.getClass("Entity").getMethod("getNBTTag").invoke(nmsEntity);

							if(tag == null)
								tag = NMSUtils.getClass("NBTTagCompound").newInstance();

							NMSUtils.getClass("Entity").getMethod("c", NMSUtils.getClass("NBTTagCompound")).invoke(nmsEntity, tag);

							if((int) NMSUtils.getClass("NBTTagCompound").getMethod("getInt", String.class).invoke(tag, "NoAI") != 1)
							{
								NMSUtils.getClass("NBTTagCompound").getMethod("setInt", String.class, int.class).invoke(tag, "NoAI", 1);
								noAI.add(ent);
							}
							NMSUtils.getClass("Entity").getMethod("f", NMSUtils.getClass("NBTTagCompound")).invoke(nmsEntity, tag);
						}

						else if(ent instanceof LivingEntity)
						{
							LivingEntity.class.getMethod("setAI", boolean.class).invoke(ent, false);
							noAI.add(ent);
						}
					}
				}
			}
		}
	}

	public void resetAIs() throws ReflectiveOperationException
	{
		for(Entity entity : noAI)
		{
			try
			{
				if(Bukkit.getBukkitVersion().contains("1.8"))
				{
					Object craftEntity = NMSUtils.getClass("CraftEntity").cast(entity);
					Object nmsEntity = NMSUtils.getClass("CraftEntity").getMethod("getHandle").invoke(craftEntity);

					Object tag = NMSUtils.getClass("Entity").getMethod("getNBTTag").invoke(nmsEntity);

					if(tag == null)
						tag = NMSUtils.getClass("NBTTagCompound").newInstance();

					NMSUtils.getClass("Entity").getMethod("c", NMSUtils.getClass("NBTTagCompound")).invoke(nmsEntity, tag);

					NMSUtils.getClass("NBTTagCompound").getMethod("setInt", String.class, int.class).invoke(tag, "NoAI", 0);

					NMSUtils.getClass("Entity").getMethod("f", NMSUtils.getClass("NBTTagCompound")).invoke(nmsEntity, tag);
				}
				else
				{
					LivingEntity.class.getMethod("setAI", boolean.class).invoke(entity, true);
				}

			}catch(Exception ex)
			{
				Bukkit.getLogger().warning("Entity at " + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockY() + ", " + entity.getLocation().getBlockZ() + " world:" + entity.getLocation().getWorld().getName() + " n'a pa retrouvé son intelligence !");
			}
		}
		noAI.clear();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(config.contains("noAI"))
			for(String id : config.getStringList("noAI"))
				for(World w : Bukkit.getWorlds())
				{
					for(Entity ent : w.getEntities())
					{
						if(!(ent instanceof Player))
						{
							if(ent.getUniqueId() == UUID.fromString(id))
							{
								noAI.add(ent);
							}
						}
					}
				}
		
		if(config.contains("UnDespawnable"))
			for(String id : config.getStringList("UnDespawnable"))
				for(World w : Bukkit.getWorlds())
				{
					for(Entity ent : w.getEntities())
					{
						if(!(ent instanceof Player))
						{
							if(ent.getUniqueId() == UUID.fromString(id))
							{
								unDespawnable.add(ent);
							}
						}
					}
				}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		List<String> noAiIds = new ArrayList<String>();
		for(Entity e : noAI)
			noAiIds.add(e.getUniqueId().toString());
		
		List<String> itemIds = new ArrayList<String>();
		for(Entity e : unDespawnable)
			itemIds.add(e.getUniqueId().toString());

		config.set("noAI", noAiIds);
		config.set("UnDespawnable", itemIds);
	}
}
