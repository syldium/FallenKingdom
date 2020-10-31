package fr.devsylone.fkpi.lockedchests;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.PlayerLockedChestInteractEvent;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

import java.util.UUID;

public class LockedChest implements Saveable
{
	public enum ChestState
	{
		LOCKED,
		UNLOCKING,
		UNLOCKED
	}

	private UUID unlocker;
	private ChatColor chatColor = ChatColor.RESET;
	private Location loc;
	private int time;
	private int day;
	private ChestState state;
	private long lastInteract;
	private long startUnlocking;
	private float yFix = -0.5F;
	private String requiredAdvancement;
	private String lootTable;

	private int task = -1;

	private String name;

	public LockedChest(Location loc, int time, int day, String name)
	{
		this.loc = loc;
		this.time = time;
		unlocker = null;
		this.day = day;
		state = ChestState.LOCKED;
		lastInteract = System.currentTimeMillis();
		this.name = name;
	}

	public Location getLocation()
	{
		return loc.clone();
	}

	public UUID getUnlocker()
	{
		return unlocker;
	}

	public int getUnlockDay()
	{
		return day;
	}

	public int getUnlockingTime()
	{
		return time;
	}

	public String getName()
	{
		return name;
	}

	public void changeUnlocker(Player newPlayer)
	{
		unlocker = newPlayer == null ? null : newPlayer.getUniqueId();
		startUnlocking = System.currentTimeMillis();

		if(newPlayer == null)
			setState(ChestState.LOCKED);
		else
			setState(ChestState.UNLOCKING);

		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(newPlayer);
		chatColor = team == null ? ChatColor.RESET : team.getChatColor();
	}

	public ChestState getState()
	{
		return state;
	}

	public void setState(ChestState state)
	{
		this.state = state;
	}

	public void updateLastInteract()
	{
		lastInteract = System.currentTimeMillis();
	}

	public void setYFixByBlockFace(BlockFace blockFace)
	{
		switch (blockFace)
		{
			case UP:
				yFix = -1.25F;
				break;
			case DOWN:
				yFix = 0;
				break;
			default:
				yFix = -0.5F;
		}
	}

	public void startUnlocking(Player player)
	{
		PlayerLockedChestInteractEvent event = new PlayerLockedChestInteractEvent(player, this); // EVENT
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
			return;

		if(unlocker != null)
			Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_ABORT.getMessage().replace("%name%", name).replace("%player%", chatColor.toString() + Bukkit.getOfflinePlayer(unlocker).getName()));

		changeUnlocker(player);
		Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_START.getMessage().replace("%name%", name).replace("%player%", chatColor + player.getName()));

		if(task > 0)
			Bukkit.getScheduler().cancelTask(task);

		lastInteract = System.currentTimeMillis();

		final int armorstand = Fk.getInstance().getPacketManager().createFloatingText("§b0%", player, loc.clone().add(0.5, yFix, 0.5));

		task = Bukkit.getScheduler().runTaskTimer(Fk.getInstance(), () -> {
			if(player.isOnline())
				Fk.getInstance().getPacketManager().updateFloatingText(armorstand, "§b" + (int) (((double) (System.currentTimeMillis() - startUnlocking) / 1000.0d / (double) time) * 100.0d) + "%");

			if(lastInteract + 1000 < System.currentTimeMillis())
			{
				if(!getState().equals(ChestState.UNLOCKED))
					Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_ABORT.getMessage().replace("%name%", name).replace("%player%", chatColor + player.getName()));
				Bukkit.getScheduler().cancelTask(task);
				changeUnlocker(null);
				if(player.isOnline())
					Fk.getInstance().getPacketManager().remove(armorstand);
			}

			if(startUnlocking + time * 1000 <= System.currentTimeMillis())
			{
				PlayerLockedChestInteractEvent endEvent = new PlayerLockedChestInteractEvent(player, this); // EVENT
				Bukkit.getPluginManager().callEvent(endEvent); // EVENT
				if(!endEvent.isCancelled())
				{
					setState(ChestState.UNLOCKED);
					Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_UNLOCKED.getMessage().replace("%name%", name).replace("%player%", chatColor + player.getName()));
				}
				Bukkit.getScheduler().cancelTask(task);
				if(player.isOnline())
					Fk.getInstance().getPacketManager().remove(armorstand);
			}
		}, 1L, 1L).getTaskId();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		loc = new Location(Bukkit.getWorld(config.getString("Loc.World")), config.getInt("Loc.x"), config.getInt("Loc.y"), config.getInt("Loc.z"));
		state = ChestState.valueOf(config.getString("State"));
		time = config.getInt("Time");
		day = config.getInt("Day");
		name = config.getString("Name");
		lootTable = config.getString("LootTable");
		requiredAdvancement = config.getString("Advancement");
	}

	@SuppressWarnings("deprecation")
	public Advancement getRequiredAdvancement()
	{
		if (requiredAdvancement == null || !requiredAdvancement.contains(":")) {
			return null;
		}
		return Bukkit.getAdvancement(new NamespacedKey(requiredAdvancement.split(":")[0], requiredAdvancement.split(":")[1]));
	}

	public void setRequiredAdvancement(String advancement)
	{
		this.requiredAdvancement = advancement;
	}

	public boolean hasAccess(Player player)
	{
		if (requiredAdvancement == null || requiredAdvancement.isEmpty()) {
			return true;
		}
		return XAdvancement.hasAdvancement(player, requiredAdvancement);
	}

	@SuppressWarnings("deprecation")
	public LootTable getLootTable()
	{
		if (lootTable == null || !lootTable.contains(":")) {
			return null;
		}
		if (Version.VersionType.V1_13.isHigherOrEqual())
			return Bukkit.getLootTable(new NamespacedKey(lootTable.split(":")[0], lootTable.split(":")[1]));
		throw new NotImplementedException("Loot tables api don't exist in versions prior to 1.13.");
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Loc.World", loc.getWorld().getName());
		config.set("Loc.x", loc.getBlockX());
		config.set("Loc.y", loc.getBlockY());
		config.set("Loc.z", loc.getBlockZ());

		config.set("State", state.name());
		config.set("Time", time);
		config.set("Day", day);
		config.set("Name", name);
		config.set("LootTable", lootTable);
		config.set("Advancement", requiredAdvancement);
	}

	@Override
	public String toString()
	{
		return (unlocker == null ? "" : unlocker) + " / " + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ() + " / " + time + " / " + day + " / " + state.name() + " / " + lastInteract + " / " + startUnlocking;
	}
}
