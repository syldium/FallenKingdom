package fr.devsylone.fkpi.lockedchests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.util.Saveable;

public class LockedChest implements Saveable
{
	public enum ChestState
	{
		LOCKED,
		UNLOCKING,
		UNLOCKED
	}

	private String unlocker;
	private Location loc;
	private int time;
	private int day;
	private ChestState state;
	private long lastInteract;
	private long startUnlocking;
	private float yFix = -0.5F;

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

	public String getUnlocker()
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

	public void changeUnlocker(String newPlayer)
	{
		unlocker = newPlayer;
		startUnlocking = System.currentTimeMillis();

		if(newPlayer == null)
			setState(ChestState.LOCKED);
		else
			setState(ChestState.UNLOCKING);
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

	public void startUnlocking(String player)
	{
		if(unlocker != null)
			Fk.broadcast("§7Crochetage du coffre §5" + name + "§7 abandonné par " + (Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker) != null ? Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker).getChatColor() : "§r") + unlocker);

		changeUnlocker(player);

		Fk.broadcast("§7Le coffre §5" + name + "§7 commence a être crocheté par " + (Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker) != null ? Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker).getChatColor() : "§r") + unlocker);

		if(task > 0)
			Bukkit.getScheduler().cancelTask(task);

		lastInteract = System.currentTimeMillis();

		final int armorstand = Fk.getInstance().getPacketManager().createFloattingText("§b0%", Bukkit.getPlayer(player), loc.clone().add(0.5, yFix, 0.5));

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fk.getInstance(), new Runnable()
		{

			@Override
			public void run()
			{
				Fk.getInstance().getPacketManager().updateFloattingText(armorstand, "§b" + (int) (((double) ((double) (System.currentTimeMillis() - startUnlocking) / 1000.0d) / (double) time) * 100.0d) + "%");
				if(lastInteract + 1000 < System.currentTimeMillis())
				{
					if(!getState().equals(ChestState.UNLOCKED))
						Fk.broadcast("§7Crochetage du coffre §5" + name + "§7 abandonné par " + (Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker) != null ? Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker).getChatColor() : "§r") + unlocker);
					Bukkit.getScheduler().cancelTask(task);
					changeUnlocker(null);
					Fk.getInstance().getPacketManager().remove(armorstand);
				}

				if(startUnlocking + time * 1000 <= System.currentTimeMillis())
				{
					setState(ChestState.UNLOCKED);
					Fk.broadcast("§7Le coffre §5" + name + "§7 a été crocheté par " + (Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker) != null ? Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(unlocker).getChatColor() : "§r") + unlocker);
					Bukkit.getScheduler().cancelTask(task);
					Fk.getInstance().getPacketManager().remove(armorstand);
				}
			}
		}, 1l, 1l);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		loc = new Location(Bukkit.getWorld(config.getString("Loc.World")), config.getInt("Loc.x"), config.getInt("Loc.y"), config.getInt("Loc.z"));
		state = ChestState.valueOf(config.getString("State"));
		time = config.getInt("Time");
		day = config.getInt("Day");
		name = config.getString("Name");
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

	}

	@Override
	public String toString()
	{
		return (unlocker == null ? "" : unlocker) + " / " + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ() + " / " + time + " / " + day + " / " + state.name() + " / " + lastInteract + " / " + startUnlocking;
	}
}
