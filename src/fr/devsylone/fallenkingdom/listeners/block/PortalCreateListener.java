package fr.devsylone.fallenkingdom.listeners.block;

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
import fr.devsylone.fallenkingdom.utils.XBlock;

import java.lang.reflect.Method;
import java.util.List;


public class PortalCreateListener implements Listener
{

	@EventHandler
	public void create(PortalCreateEvent e)
	{
		if(!Fk.getInstance().getGame().isNetherEnabled() && !e.getWorld().getName().endsWith("_nether") && e.getReason() == CreateReason.FIRE)
		{
			Block air = null;
			try {
				Method getBlocks = PortalCreateEvent.class.getDeclaredMethod("getBlocks");
				air = XBlock.getAirBlock((List<?>) getBlocks.invoke(e));
			} catch (Exception ex) {
				ex.printStackTrace(); // Dommage...
			}
			if (Fk.isDebug())
				Fk.getInstance().getLogger().info(air.getType().toString() + " en " + air.getLocation().toString());

			Fk.getInstance().getPortalsManager().addPortal(air.getLocation());
			e.setCancelled(true);
			for(Player p : Bukkit.getOnlinePlayers())
				if(p.getLocation().distance(air.getLocation()) <= 15)
				{
					if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING) && p.getGameMode().equals(GameMode.CREATIVE))
					{
						e.setCancelled(false);
						Fk.getInstance().getPlayerManager().getPlayer(p).sendMessage("§cSi vous cassez ce portail à l'aide d'un seau d'eau, il se \n§créouvrira §4automatiquement§c lors de l'activation du nether");
					}
					else
						Fk.getInstance().getPlayerManager().getPlayer(p).sendMessage("§dLe nether n'est pas encore ouvert, ce portail s'allumera §5automatiquement§d le jour §5" + Fk.getInstance().getFkPI().getRulesManager().getRuleByName("NetherCap").getValue());

				}
		}
	}
}
