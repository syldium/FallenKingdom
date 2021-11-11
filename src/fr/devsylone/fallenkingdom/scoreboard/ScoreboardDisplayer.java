package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardDisplayer
{
	private final FkPlayer fkPlayer;
	private final WeakReference<Player> player;

	private final PacketManager packetManager;

	private BukkitRunnable runnable;
	private final List<Integer> entities = new ArrayList<>();

	public ScoreboardDisplayer(FkPlayer fkPlayer)
	{
		this.fkPlayer = fkPlayer;
		this.player = new WeakReference<>(Bukkit.getPlayer(fkPlayer.getName()));
		this.packetManager = Fk.getInstance().getPacketManager();
	}

	public void display()
	{
		Player player = this.player.get();
		if (player == null)
			return;

		Location location = player.getLocation();

		entities.add(packetManager.createFloatingText(Messages.CMD_MAP_SCOREBOARD_DISPLAYER_EDIT_LINE.getMessage(), player, location));
		entities.add(packetManager.createFloatingText("§e >>>>> /fk scoreboard setLine <<<<< ", player, location));
		entities.add(packetManager.createFloatingText(Messages.CMD_MAP_SCOREBOARD_DISPLAYER_USE_WHEEL.getMessage(), player, location));
		entities.add(packetManager.createFloatingText(Messages.CMD_MAP_SCOREBOARD_DISPLAYER_LOOK_SCOREBOARD.getMessage(), player, location));

		entities.add(packetManager.createFloatingText(Messages.CMD_MAP_SCOREBOARD_DISPLAYER_VARIABLES.getMessage(), player, location));

		for(PlaceHolder ph : PlaceHolder.values())
			entities.add(packetManager.createFloatingText("§8" + ph.getDescription() + "        §c->§r        " + ph.getKey(), player, location));

		entities.add(packetManager.createFloatingText(Messages.CMD_MAP_SCOREBOARD_DISPLAYER_LEAVE_EDIT.getMessage(), player, location));

		startUpdateRunnable();
		fkPlayer.refreshScoreboard();
	}

	public void updateLoc(Player player)
	{
		Location loc = player.getLocation().add(0, 1, 0);
		loc.setPitch(0.0f);
		loc = getSight(loc, 10);
		loc.setY(loc.getY() + 0.25 * ((float) entities.size() / 2) - 1);

		for (int entity : entities) {
			Fk.getInstance().getPacketManager().updateFloatingText(entity, loc);
			loc.add(0, -0.25, 0);
		}
	}

	private Location getSight(final Location loc, int limit)
	{
		Location end = loc;
		Vector direction = loc.getDirection();

		for (int offset = 0; offset < limit; offset++) {
			float off = (float) offset / 2;
			Location location = loc.clone().add(direction.getX() * off, direction.getY() * off, direction.getZ() * off);
			if (location.getBlock().getType() != Material.AIR) {
				return end;
			}
			end = location;
		}

		return end;
	}

	public void exit()
	{
		if (runnable != null) {
			runnable.cancel();
			runnable = null;
		}

		for(int id : entities)
			packetManager.remove(id);
		entities.clear();

		fkPlayer.setUseFormattedText(true);
	}

	public void startUpdateRunnable()
	{
		runnable = new BukkitRunnable() {

			private double lastX = 0;
			private double lastY = 0;
			private double lastZ = 0;
			private float lastYaw = 0;
			private float lastPitch = 0;

			@Override
			public void run() {
				Player p = player.get();
				if (p == null) {
					this.cancel();
					return;
				}

				Location loc = p.getLocation();
				if (
						Double.compare(lastX, loc.getX()) != 0 || Double.compare(lastY, loc.getY()) != 0 || Double.compare(lastZ, loc.getZ()) != 0
								|| Double.compare(lastYaw, loc.getYaw()) != 0 || Double.compare(lastPitch, loc.getPitch()) != 0
				)
				{
					lastX = loc.getX();
					lastY = loc.getY();
					lastZ = loc.getZ();
					lastYaw = loc.getYaw();
					lastPitch = loc.getPitch();
					updateLoc(p);
				}
			}
		};
		runnable.runTaskTimer(Fk.getInstance(), 5L, 3L);
	}
}
