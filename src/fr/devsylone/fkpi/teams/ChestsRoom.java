package fr.devsylone.fkpi.teams;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.ChestRoomRunnable;
import fr.devsylone.fallenkingdom.version.packet.block.MultiBlockChange;
import fr.devsylone.fallenkingdom.version.packet.entity.Hologram;
import fr.devsylone.fallenkingdom.version.packet.entity.ItemSlot;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.TeamCaptureEvent;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestsRoom implements Saveable
{
	private Location min;
	private Location max;

	private final List<Location> chests = new ArrayList<>();
	private final List<UUID> enemyInside = new ArrayList<>();
	private ChestRoomState state = ChestRoomState.NORMAL;

	private final Base base;

	private BukkitTask captureTask;
	private Team captureTeam;

	public enum ChestRoomState
	{
		NORMAL,
		CAPTURING,
		CAPTURED
	}

	public ChestsRoom(Base base)
	{
		this.base = base;
	}

	public static boolean isIn(Location test, Location min, Location max, int offset)
	{
		return min != null && test.getX() >= Math.min(min.getX(), max.getX()) + offset && test.getY() >= Math.min(min.getY(), max.getY()) + offset && test.getZ() >= Math.min(min.getZ(), max.getZ()) + offset && test.getX() <= Math.max(min.getX(), max.getX()) - offset && test.getY() <= Math.max(min.getY(), max.getY()) - offset && test.getZ() <= Math.max(min.getZ(), max.getZ() - offset);
	}

	public boolean contains(Location test)
	{
		return isIn(test, min, max, 0);
	}

	public void removeChest(Location loc)
	{
		if(!chests.contains(loc))
			return;

		chests.remove(loc);

		if(chests.isEmpty())
		{
			min = null;
			max = null;
		}

		else
		{
			min = null;
			max = null;
			for(Location chest : chests)
			{
				newChest(chest);
			}
		}
	}

	public void newChest(Location loc)
	{
		if(!chests.contains(loc))
			chests.add(loc);

		if(min == null)
		{
			max = loc.clone().add(getOffset(), getOffset(), getOffset());
			min = loc.clone().add(-getOffset(), -getOffset(), -getOffset());
		}

		else if(!isIn(loc, min, max, getOffset()))
		{
			if(loc.getX() > max.getX() - getOffset())
				max.setX(loc.getX() + getOffset());

			else if(loc.getX() < min.getX() + getOffset())
				min.setX(loc.getX() - getOffset());

			if(loc.getY() > max.getY() - getOffset())
				max.setY(loc.getY() + getOffset());

			else if(loc.getY() < min.getY() + getOffset())
				min.setY(loc.getY() - getOffset());

			if(loc.getZ() > max.getZ() - getOffset())
				max.setZ(loc.getZ() + getOffset());

			else if(loc.getZ() < min.getZ() + getOffset())
				min.setZ(loc.getZ() - getOffset());
		}
	}

	public void show(final Player p, final int seetime)
	{
		if(chests.isEmpty() || min == null)
			throw new IllegalStateException(Messages.CMD_ERROR_NO_CHEST_ROOM.getMessage());

		final Location initLoc = p.getLocation().clone();

		double xDif = max.getX() - min.getX();
		double yDif = max.getY() - min.getY();
		double zDif = max.getZ() - min.getZ();
		MultiBlockChange change = MultiBlockChange.create();
		for(int ix = 0; ix <= Math.abs(xDif); ix++)
			for(int iy = 0; iy <= Math.abs(yDif); iy++)
				for(int iz = 0; iz <= Math.abs(zDif); iz++)
				{
					final Location loc = min.clone().add(xDif < 0 ? -ix : ix, yDif < 0 ? -iy : iy, zDif < 0 ? -iz : iz);
					int inter = ix == Math.abs(xDif) ? 1 : 0;
					inter = inter + (iy == Math.abs(yDif) ? 1 : 0);
					inter = inter + (iz == Math.abs(zDif) ? 1 : 0);
					inter = inter + (ix == 0 ? 1 : 0);
					inter = inter + (iy == 0 ? 1 : 0);
					inter = inter + (iz == 0 ? 1 : 0);

					if(!loc.equals(p.getLocation().add(0, -1, 0).getBlock().getLocation()) && XBlock.isBlockInCave(loc.getBlock().getType()) || loc.getBlock().getType() == Material.SAND || loc.getBlock().getType() == Material.COBBLESTONE || loc.getBlock().getType() == Material.DIRT || loc.getBlock().getType() == XMaterial.GRASS_BLOCK.parseMaterial() || loc.getBlock().getType() == Material.GRAVEL || loc.getBlock().getType().name().contains("ORE"))
						change.change(loc.getBlock(), Material.AIR);

					int id = -1;

					if(inter > 0)
					{
						if(inter > 1)
							id = Hologram.INSTANCE.displayItem(ItemSlot.HEAD, p, loc.add(0.5, -1, 0.5), Material.CHEST);

						else if(min.distanceSquared(max) <= 20*20)
							id = Hologram.INSTANCE.displayItem(ItemSlot.MAINHAND, p, loc.add(1, 0, 0), Material.CHEST);

					}

					final int finalid = id;

					Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
						if (finalid > 0) {
							Hologram.INSTANCE.remove(p, finalid);
						}
					}, Math.abs(seetime) * 20L);
				}

		change.send(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
			change.cancel(p);
		}, Math.abs(seetime) * 20L);

		new BukkitRunnable()
		{
			int i = 0;

			@Override
			public void run()
			{
				if(++i >= seetime)
					cancel();

				if(p.getLocation().getBlockX() == initLoc.getBlockX() && p.getLocation().getBlockZ() == initLoc.getBlockZ())
					return;
				initLoc.setYaw(p.getLocation().getYaw());
				initLoc.setPitch(p.getLocation().getPitch());
				p.teleport(initLoc.clone().add(0, 0.2, 0));
			}
		}.runTaskTimer(Fk.getInstance(), 20L, 20L);
	}

	public void addEnemyInside(Player player)
	{
		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (team != null && !team.equals(base.getTeam()))
		{
			enemyInside.add(player.getUniqueId());
			if (((float) enemyInside.size() / team.getPlayers().size()) * 100 >= FkPI.getInstance().getRulesManager().getRule(Rule.CAPTURE_RATE)) {
				startCapture(team);
			}
		}
	}

	public void removeEnemyInside(Player player)
	{
		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (team != null && !team.equals(base.getTeam()))
		{
			enemyInside.remove(player.getUniqueId());

			if (((float) enemyInside.size() / team.getPlayers().size()) * 100 >= FkPI.getInstance().getRulesManager().getRule(Rule.CAPTURE_RATE)) {
				return;
			}

			if(captureTeam != null && captureTeam.equals(team) && state != ChestRoomState.CAPTURED)
			{
				for(String mate : team.getPlayers())
				{
					Player p = Bukkit.getPlayer(mate);
					if (p != null) {
						ChatUtils.sendMessage(p,
								team.getChatColor() + Messages.PLAYER_CHEST_ROOM_CAPTURE_INTERRUPTED.getMessage()
										.replace("%player%", player.getDisplayName())
						);
					}
				}

				captureTeam = null;
				captureTask.cancel();
				state = ChestRoomState.NORMAL;
			}
		}
	}

	public List<UUID> getEnemiesInside() {
		return enemyInside;
	}

	public void startCapture(final Team team)
	{
		if (state != ChestRoomState.NORMAL)
			return;

		state = ChestRoomState.CAPTURING;
		captureTeam = team;

		Bukkit.getServer().getPluginManager().callEvent(new TeamCaptureEvent(team, base.getTeam(), false));  // EVENT

		for(String player : team.getPlayers())
		{
			Player p = Bukkit.getPlayer(player);
			if (p != null) {
				ChatUtils.sendMessage(p, team.getChatColor() + Messages.PLAYER_CHEST_ROOM_CAPTURE_STARTED.getMessage());
			}
		}

		captureTask = new ChestRoomRunnable(this, team, base.getTeam()).runTaskTimer(Fk.getInstance(), 5L, 5L);
	}

	public ChestRoomState getState()
	{
		return state;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(!config.isConfigurationSection("Chests"))
			return;
		for(String key : config.getConfigurationSection("Chests").getKeys(false))
			newChest(new Location(Bukkit.getWorld(config.getString("Chests." + key + ".World")), config.getInt("Chests." + key + ".X"), config.getInt("Chests." + key + ".Y"), config.getInt("Chests." + key + ".Z")));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		if(chests.isEmpty())
			return;
		for(int i = 0; i < chests.size(); i++)
		{
			Location chest = chests.get(i);
			config.set("Chests." + i + ".World", chest.getWorld().getName());
			config.set("Chests." + i + ".X", chest.getBlockX());
			config.set("Chests." + i + ".Y", chest.getBlockY());
			config.set("Chests." + i + ".Z", chest.getBlockZ());
		}
	}

	private int getOffset()
	{
		return FkPI.getInstance().getChestsRoomsManager().getOffset();
	}

	public boolean exists()
	{
		return !chests.isEmpty();
	}
}
