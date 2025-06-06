package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Nexus;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fallenkingdom.display.notification.RegionChange;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static fr.devsylone.fallenkingdom.listeners.entity.player.PauseInteractionListener.isCancelledDueToPause;

public class MoveListener implements Listener
{
	private final Map<UUID, Location> onTnt = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void move(PlayerMoveEvent e)
	{
		if(e.getTo() == null || !Fk.getInstance().getWorldManager().isAffected(e.getTo().getWorld()))
			return;

		/*
		 * IF changé de block X-Y-Z
		 */

		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());
		fkp.updateDisplay(e.getPlayer(), PlaceHolder.LOCATION_RELATIVE);

		checkTnt(e);

		if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY())
			return;

		/*
		 * A partir de maintenant, juste si le fromXYZ != toXYZ
		 */

		if(Fk.getInstance().getGame().isPaused() && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE) && e.getFrom().getBlockY() == e.getTo().getBlockY())
		{
			if(e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR)
				return;
			
			fkp.sendMessage(Messages.PLAYER_PAUSE);
			Location tp = e.getFrom().getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
			tp.setPitch(e.getFrom().getPitch());
			tp.setYaw(e.getFrom().getYaw());

			e.getPlayer().teleport(tp);

			return;
		}

		for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
			Base base = team.getBase();
			if (base == null) {
				continue;
			}
			boolean wasInside = base.contains(e.getFrom());
			boolean isInside = base.contains(e.getTo());
			RegionChange change = null;
			if (!wasInside && isInside) {
				change = new RegionChange(base, RegionChange.MoveType.ENTER);
			} else if (wasInside && !isInside) {
				change = new RegionChange(base, RegionChange.MoveType.LEAVE);
			}

			Nexus nexus = base.getNexus();
			if (FkPI.getInstance().getChestsRoomsManager().isEnabled() && e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
				if (nexus.contains(e.getTo()) && !nexus.isInside(e.getPlayer())) {
					if (nexus.isDefined()) {
						change = new RegionChange(nexus, RegionChange.MoveType.ENTER);
					}
 					nexus.addEnemyInside(e.getPlayer());
				} else if (!nexus.contains(e.getTo()) && nexus.isInside(e.getPlayer())) {
					if (nexus.isDefined()) {
						change = new RegionChange(nexus, RegionChange.MoveType.LEAVE);
					}
					nexus.removeEnemyInside(e.getPlayer());
                }
			}
			if (change != null) {
				fkp.setLastChange(change);
				Fk.getInstance().getDisplayService().dispatch(change, e.getPlayer());
				Fk.getInstance().getDisplayService().update(e.getPlayer(), fkp, PlaceHolder.REGION, PlaceHolder.REGION_CHANGE);
			}
		}
	}

	private void checkTnt(PlayerMoveEvent event)
	{
		if (FkPI.getInstance().getRulesManager().getRule(Rule.TNT_JUMP) || !Version.VersionType.V1_13.isHigherOrEqual()) {
			return;
		}
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		final Location tntLocation = player.getLocation();
		final TntState state = TntListener.isStandingOnTnt(player, tntLocation);
		if (state == TntState.OTHER_SUPPORT) {
			this.onTnt.remove(player.getUniqueId());
			return;
		}
		if (state == TntState.UNDEFINED) {
			return;
		}
		final Location previousTntLocation = this.onTnt.put(player.getUniqueId(), tntLocation);
		if (previousTntLocation == null || previousTntLocation.equals(tntLocation)) {
			return;
		}

		final Optional<Base> surroundingBase = FkPI.getInstance().getTeamManager().getBase(tntLocation, 8);
		if (!surroundingBase.isPresent()) {
			return;
		}
		final Team team = surroundingBase.get().getTeam();
		if (Objects.equals(team, FkPI.getInstance().getTeamManager().getPlayerTeam(player))) {
			return;
		}
		this.onTnt.remove(player.getUniqueId());

		previousTntLocation.add(0.5, 1, 0.5);
		previousTntLocation.setPitch(event.getFrom().getPitch());
		previousTntLocation.setYaw(event.getFrom().getYaw());
		event.setTo(previousTntLocation);
		ChatUtils.sendMessage(player, Messages.PLAYER_TNT_JUMP_DENIED);
	}

	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent e) {
		TeleportCause cause = e.getCause();
		Player player = e.getPlayer();
		Location to = e.getTo();
		if (isCancelledDueToPause(player)) {
			e.setCancelled(true);
			return;
		}
		if (!Fk.getInstance().getWorldManager().isWorldWithBase(to.getWorld())) {
			return;
		}
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}

		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());
		TeleportCause chorus = Version.VersionType.V1_9_V1_12.isHigherOrEqual() ? TeleportCause.CHORUS_FRUIT : null;
		if (cause == TeleportCause.NETHER_PORTAL && !FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_ASSAULT)) {
			Base base = FkPI.getInstance().getTeamManager().getBase(to).orElse(null);
			if (base != null && !base.getTeam().equals(pTeam)) {
				ChatUtils.sendMessage(player, Messages.PLAYER_NETHER_PORTAL_PAIR_IN_ENEMY_BASE.getMessage().replace("%team%", base.getTeam().toString()));
				e.setCancelled(true);
			}
		} else if ((cause == TeleportCause.ENDER_PEARL || cause == chorus) && !FkPI.getInstance().getRulesManager().getRule(Rule.ENDERPEARL_ASSAULT)) {
			Base base = FkPI.getInstance().getTeamManager().getBase(to).orElse(null);
			if (base != null && !base.getTeam().equals(pTeam) && !base.contains(e.getFrom(), -1)) {
				ChatUtils.sendMessage(player, Messages.PLAYER_TP_IN_BASE.getMessage().replace("%team%", base.getTeam().toString()));
				e.setCancelled(true);
			}
		}
	}

	private enum TntState {
		UNDEFINED,
		OTHER_SUPPORT,
		ON_TNT
	}

	// In a separate class to avoid loading it if the server version is not supported
	private static class TntListener {
		private static @NotNull List<Block> getBlocksBelow(@NotNull Entity entity) {
			final BoundingBox aabb = entity.getBoundingBox();
			final Location min = new Location(entity.getWorld(), aabb.getMinX(), aabb.getMinY() - 0.2, aabb.getMinZ());
			final List<Block> blocks = new ArrayList<>(4);
			blocks.add(min.getBlock());
			blocks.add(min.clone().add(aabb.getWidthX(), 0, 0).getBlock());
			blocks.add(min.clone().add(0, 0, aabb.getWidthZ()).getBlock());
			blocks.add(min.clone().add(aabb.getWidthX(), 0, aabb.getWidthZ()).getBlock());
			return blocks;
		}

		private static boolean isStandingOn(@NotNull BoundingBox blockAABB, @NotNull BoundingBox playerAABB) {
			return playerAABB.overlaps(blockAABB.shift(0, 0.1, 0));
		}

		public static @NotNull TntState isStandingOnTnt(@NotNull Entity entity, @NotNull Location tntLocation) {
			TntState state = TntState.UNDEFINED;
			for (Block block : getBlocksBelow(entity)) {
				if (block.isPassable()) {
					continue;
				}
				final VoxelShape collisionShape = block.getCollisionShape();
				for (BoundingBox aabb : collisionShape.getBoundingBoxes()) {
					aabb.shift(block.getX(), block.getY(), block.getZ());
					if (isStandingOn(aabb, entity.getBoundingBox())) {
						if (block.getType() == Material.TNT) {
							block.getLocation(tntLocation);
							state = TntState.ON_TNT;
						} else {
							state = TntState.OTHER_SUPPORT;
						}
					}
				}
			}
			return state;
		}
	}
}
