package fr.devsylone.fkpi.teams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.devsylone.fkpi.api.event.TeamCaptureEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.cryptomorin.xseries.XMaterial;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.util.Saveable;

public class ChestsRoom implements Saveable
{
	private Location min;
	private Location max;

	private List<Location> chests;

	private List<String> enemyInside;

	private Base base;

	private ChestRoomState state;

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
		chests = new ArrayList<Location>();
		enemyInside = new ArrayList<String>();
		this.base = base;
		state = ChestRoomState.NORMAL;
	}

	public static boolean isIn(Location test, Location min, Location max, int offset)
	{
		return min == null ? false : test.getX() >= Math.min(min.getX(), max.getX()) + offset && test.getY() >= Math.min(min.getY(), max.getY()) + offset && test.getZ() >= Math.min(min.getZ(), max.getZ()) + offset && test.getX() <= Math.max(min.getX(), max.getX()) - offset && test.getY() <= Math.max(min.getY(), max.getY()) - offset && test.getZ() <= Math.max(min.getZ(), max.getZ() - offset);
	}

	public boolean contains(Location test)
	{
		return isIn(test, min, max, 0);
	}

	public void removeChest(Location loc)
	{
		if(!chests.contains(loc.getBlock().getLocation()))
			return;

		chests.remove(loc.getBlock().getLocation());

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
		if(!chests.contains(loc.getBlock().getLocation()))
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
			throw new IllegalStateException("Pas de salle des coffres");

		final Location initLoc = p.getLocation().clone();

		double xDif = max.getX() - min.getX();
		double yDif = max.getY() - min.getY();
		double zDif = max.getZ() - min.getZ();
		final Set<Chunk> toReset = new HashSet<Chunk>();
		for(int ix = 0; ix <= Math.abs(xDif); ix++)
			for(int iy = 0; iy <= Math.abs(yDif); iy++)
				for(int iz = 0; iz <= Math.abs(zDif); iz++)
				{
					final Location loc = min.clone().add(xDif < 0 ? -ix : ix, yDif < 0 ? -iy : iy, zDif < 0 ? -iz : iz);
					int inter = 0;
					inter = inter + (ix == Math.abs(xDif) ? 1 : 0);
					inter = inter + (iy == Math.abs(yDif) ? 1 : 0);
					inter = inter + (iz == Math.abs(zDif) ? 1 : 0);
					inter = inter + (ix == 0 ? 1 : 0);
					inter = inter + (iy == 0 ? 1 : 0);
					inter = inter + (iz == 0 ? 1 : 0);

					if(!loc.equals(p.getLocation().add(0, -1, 0).getBlock().getLocation()) && XBlock.isBlockInCave(loc.getBlock().getType()) || loc.getBlock().getType() == Material.SAND || loc.getBlock().getType() == Material.COBBLESTONE || loc.getBlock().getType() == Material.DIRT || loc.getBlock().getType() == XMaterial.GRASS_BLOCK.parseMaterial() || loc.getBlock().getType() == Material.GRAVEL || loc.getBlock().getType().name().contains("ORE"))
					{
						Fk.getInstance().getPacketManager().sendBlockChange(p, loc, Material.AIR);
						toReset.add(loc.getChunk());
					}

					int id = -1;

					if(inter > 0)
					{
						if(inter > 1)
							id = Fk.getInstance().getPacketManager().displayItem(PacketManager.BIG_ITEM, p, loc.add(0.5, -1, 0.5), Material.CHEST);

						else if(min.distance(max) <= 20)
							id = Fk.getInstance().getPacketManager().displayItem(PacketManager.SMALL_ITEM, p, loc.add(1, 0, 0), Material.CHEST);

					}

					final int finalid = id;

					Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
					{

						@Override
						public void run()
						{
							if(finalid > 0)
								Fk.getInstance().getPacketManager().remove(finalid);
						}
					}, Math.abs(seetime) * 20l);
				}

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{

			@Override
			public void run()
			{
				for(Chunk c : toReset)
					Fk.getInstance().getPacketManager().sendChunkReset(p, c);

			}
		}, Math.abs(seetime) * 20l);

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
		}.runTaskTimer(Fk.getInstance(), 20l, 20l);
	}

	public void addEnemyInside(String player)
	{

		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if(team != null && !team.equals(base.getTeam()))
		{
			enemyInside.add(player);
			boolean allMates = true;
			for(String mate : team.getPlayers())
				if(!enemyInside.contains(mate))
				{
					allMates = false;
					break;
				}

			if(allMates)
			{
				startCapture(team);
			}
		}
	}

	public void removeEnemyInside(String player)
	{
		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if(team != null && !team.equals(base.getTeam()))
		{
			enemyInside.remove(player);

			if(captureTeam != null && captureTeam.equals(team) && state != ChestRoomState.CAPTURED)
			{
				for(String mate : team.getPlayers())
					if(Bukkit.getPlayer(mate) != null)
						Fk.getInstance().getPlayerManager().getPlayer(mate).sendMessage(team.getChatColor() + "[Équipe]§r " + player + " est sorti de la salle des coffres, capture §cannulée");

				captureTeam = null;
				captureTask.cancel();
				state = ChestRoomState.NORMAL;
			}
		}
	}

	public void startCapture(final Team team)
	{
		if(state != ChestRoomState.CAPTURING && state != ChestRoomState.CAPTURED)
			state = ChestRoomState.CAPTURING;
		else
			return;

		captureTeam = team;

		Bukkit.getServer().getPluginManager().callEvent(new TeamCaptureEvent(team, base.getTeam(), false));  // EVENT

		for(String player : team.getPlayers())
			if(Bukkit.getPlayer(player) != null)
				Fk.getInstance().getPlayerManager().getPlayer(player).sendMessage(team.getChatColor() + "[Équipe]§r Vous commencez la capture de la salle des coffres  !");

		final long startCaptureTimestamp = System.currentTimeMillis();
		captureTask = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(System.currentTimeMillis() >= startCaptureTimestamp + FkPI.getInstance().getChestsRoomsManager().getCaptureTime() * 1000)
				{
					state = ChestRoomState.CAPTURED;
					Fk.broadcast("\n\n\n\n§dL'équipe " + team.toString() + " §da capturé la salle des coffres de l'équipe " + base.getTeam().toString());
					Fk.broadcast("");
					Fk.broadcast("");
					for(Player p : Bukkit.getOnlinePlayers())
						Fk.getInstance().getPacketManager().sendTitle(p, "Équipe " + base.getTeam().toString(), "§cÉliminée", 10, 60, 10);

					if(FkPI.getInstance().getTeamManager().getTeams().size() > 2)
						Bukkit.dispatchCommand((CommandSender) Bukkit.getOnlinePlayers().toArray()[0], "fk game pause");

					else
					{
						Bukkit.getServer().getPluginManager().callEvent(new TeamCaptureEvent(team, base.getTeam(), true)); // EVENT
						if(Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false))
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"function fallenkingdom:win");
						for(Player p : Bukkit.getOnlinePlayers())
							Fk.getInstance().getPacketManager().sendTitle(p, "Victoire !!", "Gagnants : " + team.toString(), 10, 10 * 20, 10);
						new BukkitRunnable()
						{
							private int i;

							@Override
							public void run()
							{
								if(++i >= 20)
									cancel();
								for(String player : team.getPlayers())
								{
									if(player != null && Bukkit.getPlayer(player) != null)
									{
										Firework fw = (Firework) Bukkit.getPlayer(player).getWorld().spawn(Bukkit.getPlayer(player).getLocation(), Firework.class);
										FireworkMeta meta = fw.getFireworkMeta();
										meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(188, 166, 22), Color.GREEN).build());
										fw.setFireworkMeta(meta);
										fw.setVelocity(fw.getVelocity().multiply(0.2d));
									}
								}
							}
						}.runTaskTimer(Fk.getInstance(), 20l, 20l);
					}

					cancel();

				}
				else
					for(String player : team.getPlayers())
						if(Bukkit.getPlayer(player) != null)
						{
							if(!contains(Bukkit.getPlayer(player).getLocation()) || Bukkit.getPlayer(player).isDead())
								removeEnemyInside(player);

							Fk.getInstance().getPacketManager().sendTitle(Bukkit.getPlayer(player), "", "§b" + (int) ((System.currentTimeMillis() - startCaptureTimestamp) / 1000.0d / (double) FkPI.getInstance().getChestsRoomsManager().getCaptureTime() * 100) + "%", 0, 20, 20);
						}
			}
		}.runTaskTimer(Fk.getInstance(), 5l, 5l);
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
