package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortalCreateListener implements Listener {

	private static final CreateReason NETHER_PAIR;

	static {
		CreateReason reason = null;
		try {
			reason = CreateReason.valueOf("NETHER_PAIR");
		} catch (IllegalArgumentException ignored) {}
		NETHER_PAIR = reason;
	}

	@EventHandler
	public void create(PortalCreateEvent e) {
		if (!Fk.getInstance().getWorldManager().isAffected(e.getWorld())) {
			return;
		}
		if (Fk.getInstance().getWorldManager().isWorldWithBase(e.getWorld())) {
			Team playerTeam = null;
			if (e.getEntity() instanceof Player) {
				playerTeam = FkPI.getInstance().getTeamManager().getPlayerTeam((Player) e.getEntity());
			}
			if (e.getReason() == NETHER_PAIR && !FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_ASSAULT)) {
				Base base = null;
				for (Block block : getBlocks(e)) {
					base = FkPI.getInstance().getTeamManager().getBase(block).orElse(null);
					if (base != null) {
						break;
					}
				}
				if (base != null && !base.getTeam().equals(playerTeam)) {
					if (e.getEntity() != null) {
						ChatUtils.sendMessage(e.getEntity(), Messages.PLAYER_NETHER_PORTAL_PAIR_IN_ENEMY_BASE.getMessage().replace("%team%", base.getTeam().toString()));
					}
					e.setCancelled(true);
                }
			}
		}
		if (!Fk.getInstance().getGame().isNetherEnabled() && e.getReason() == CreateReason.FIRE) {
			Block air = XBlock.getAirBlock(getBlocks(e));
			if (air == null) {
				return;
			}
			Fk.getInstance().getPortalsManager().addPortal(air.getLocation());
			e.setCancelled(true);
			for(Player p : Bukkit.getOnlinePlayers())
				if(p.getLocation().distanceSquared(air.getLocation()) <= 15*15)
				{
					if(Fk.getInstance().getGame().isPreStart() && p.getGameMode().equals(GameMode.CREATIVE))
					{
						e.setCancelled(false);
						p.sendMessage(Messages.PLAYER_NETHER_PORTAL_SETUP.getMessage());
					}
					else
						p.sendMessage(Messages.PLAYER_NETHER_PORTAL_TOO_EARLY.getMessage().replace("%day%", String.valueOf(FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP))));
				}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Block> getBlocks(PortalCreateEvent event) {
		if (Version.VersionType.V1_13.isHigherOrEqual()) {
			final List<BlockState> blockStates = event.getBlocks();
			final List<Block> blocks = new ArrayList<>(blockStates.size());
			for (BlockState blockState : blockStates) {
				blocks.add(blockState.getBlock());
			}
			return blocks;
		} else {
			try {
				// Returns an explicit ArrayList<Block>
				Method getBlocksMethod = PortalCreateEvent.class.getMethod("getBlocks");
				return (List<Block>) getBlocksMethod.invoke(event);
			} catch (ReflectiveOperationException ex) {
				return Collections.emptyList();
			}
		}
	}
}
