package fr.devsylone.fallenkingdom.listeners.entity.player;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.particles.FastParticle;
import fr.devsylone.fallenkingdom.particles.ParticleType;
import fr.devsylone.fallenkingdom.utils.RandomColor;

import org.bukkit.event.Listener;

public class Particles implements Listener {
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	private HashMap<UUID, Location> playerLocation =new HashMap<UUID,Location>();
	private HashMap<UUID,Long> movingTimer =new HashMap<UUID,Long>();
	 
	private Long movingInterval=250L;
	 
	private UUID playerID;
	 
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event)
	{
	 
	Player player = event.getPlayer();
	playerID=player.getUniqueId();
	 
	// initialize hashmaps
	 
	if(movingTimer.get(playerID)==null){movingTimer.put(playerID, System.currentTimeMillis());}
	if(playerLocation.get(playerID)==null){playerLocation.put(playerID,player.getLocation());}
	 
	// run code every movingInterval milliseconds
	 
	if(System.currentTimeMillis()-movingTimer.get(playerID)>=movingInterval)
	{
	 
	// store new time
	 
	movingTimer.put(playerID, System.currentTimeMillis());
	 
	// get stored location
	 
	Location playerLoc = playerLocation.get(playerID);
	 
	// get current location
	 
	int playerX =player.getLocation().getBlockX();
	int playerY=player.getLocation().getBlockY();
	int playerZ=player.getLocation().getBlockZ();
	 
	// player has moved after last check
	 
	if (Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING) {
	
		if(playerLoc.getBlockX()!=playerX || playerLoc.getBlockY()!=playerY || playerLoc.getBlockZ()!=playerZ)
			{
			
		FastParticle.spawnParticle(player, ParticleType.REDSTONE, player.getLocation(), 100, RandomColor.getColor(getRandomNumberInRange(0, 17)));
		}
			}
	else;
		}
	}
}
