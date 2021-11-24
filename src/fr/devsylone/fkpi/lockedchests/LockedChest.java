package fr.devsylone.fkpi.lockedchests;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.progress.ProgressBar;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.PlayerLockedChestInteractEvent;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

import java.util.UUID;

import static fr.devsylone.fallenkingdom.utils.KeyHelper.parseKey;

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
	private int day;
	private ChestState state = ChestState.LOCKED;
	private long time;
	private long lastInteract;
	private long startUnlocking;
	private float yFix = -0.5F;
	private String requiredAdvancement;
	private String lootTable;

	private int task = -1;

	private String name;

	public LockedChest(Location loc, int timeSecs, int day, String name)
	{
		this.loc = loc;
		this.time = timeSecs * 1000L;
		this.day = day;
		this.lastInteract = System.currentTimeMillis();
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

	/**
	 * @deprecated {@link #getUnlockingTimeSecs()}
	 */
	@Deprecated
	public int getUnlockingTime()
	{
		return getUnlockingTimeSecs();
	}

	public int getUnlockingTimeSecs()
	{
		return (int) (time / 1000);
	}

	public long getUnlockingTimeMillis()
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

		final Location loc = this.loc.clone().add(0.5, this.yFix, 0.5);
		final ProgressBar bar = Fk.getInstance().getDisplayService().initProgressBar(player, loc);

		task = Bukkit.getScheduler().runTaskTimer(Fk.getInstance(), () -> {
			double progress = (double) (System.currentTimeMillis() - this.startUnlocking) / this.time;
			loc.setY(this.loc.getY() + this.yFix);
			if(player.isOnline())
				bar.progress(player, loc, progress);

			if(lastInteract + 1000 < System.currentTimeMillis())
			{
				if(!getState().equals(ChestState.UNLOCKED))
					Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_ABORT.getMessage().replace("%name%", name).replace("%player%", chatColor + player.getName()));
				Bukkit.getScheduler().cancelTask(task);
				changeUnlocker(null);
				if(player.isOnline())
					bar.remove(player);
			}

			if(startUnlocking + time <= System.currentTimeMillis())
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
					bar.remove(player);
			}
		}, 1L, 1L).getTaskId();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		loc = new Location(Bukkit.getWorld(config.getString("Loc.World")), config.getInt("Loc.x"), config.getInt("Loc.y"), config.getInt("Loc.z"));
		state = ChestState.valueOf(config.getString("State"));
		time = config.getInt("Time") * 1000L;
		day = config.getInt("Day");
		name = config.getString("Name");
		lootTable = config.getString("LootTable");
		requiredAdvancement = config.getString("Advancement");
	}

	public Advancement getRequiredAdvancement()
	{
		if (requiredAdvancement == null || requiredAdvancement.isEmpty()) {
			return null;
		}
		return Bukkit.getAdvancement(parseKey(requiredAdvancement));
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

	public LootTable getLootTable()
	{
		if (lootTable == null || lootTable.isEmpty()) {
			return null;
		}
		if (Version.VersionType.V1_13.isHigherOrEqual())
			return Bukkit.getLootTable(parseKey(lootTable));
		throw new UnsupportedOperationException("Loot tables api don't exist in versions prior to 1.13.");
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Loc.World", loc.getWorld().getName());
		config.set("Loc.x", loc.getBlockX());
		config.set("Loc.y", loc.getBlockY());
		config.set("Loc.z", loc.getBlockZ());

		config.set("State", state.name());
		config.set("Time", getUnlockingTimeSecs());
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
