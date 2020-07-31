package fr.devsylone.fallenkingdom.listeners.block;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;


public class PortalCreateListener implements Listener
{

	@EventHandler
	public void create(PortalCreateEvent e)
	{
		if (!Fk.getInstance().getWorldManager().isWorldWithBase(e.getWorld()))
			return;

		if(!Fk.getInstance().getGame().isNetherEnabled() && e.getReason().equals(CreateReason.FIRE))
		{
			Block air = null;
			try {
				Method getBlocks = PortalCreateEvent.class.getDeclaredMethod("getBlocks");
				air = XBlock.getAirBlock((List<?>) getBlocks.invoke(e));
			} catch (Exception ex) {
				ex.printStackTrace(); // Dommage...
			}
			if (Fk.isDebugMode())
				Fk.getInstance().getLogger().info(air.getType().toString() + " en " + air.getLocation().toString());

			Fk.getInstance().getPortalsManager().addPortal(air.getLocation());
			e.setCancelled(true);
			for(Player p : Bukkit.getOnlinePlayers())
				if(p.getLocation().distance(air.getLocation()) <= 15)
				{
					if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING) && p.getGameMode().equals(GameMode.CREATIVE))
					{
						e.setCancelled(false);
						p.sendMessage(Messages.PLAYER_NETHER_PORTAL_SETUP.getMessage());
					}
					else
						p.sendMessage(Messages.PLAYER_NETHER_PORTAL_TOO_EARLY.getMessage().replace("%day%", String.valueOf(FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP))));
				}
		}
	}
}
