package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import fr.devsylone.fallenkingdom.Fk;


public class PingMOTD implements Listener {
	
	@EventHandler
    public void countDown(final ServerListPingEvent event){
		String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		VERSION = VERSION.replace("v", "");
		VERSION = VERSION.replace("_", ".");
		//VERSION = VERSION.replace("R", "");
		VERSION = VERSION.split(".R")[0];
		
		switch(Fk.getInstance().getGame().getState()) {
		  case BEFORE_STARTING:
			  event.setMotd("§aLa partie n'a pas encore commencé. \n§7" + "Version" + " §8>> §7" + VERSION);
		    break;
		  case STARTING:
			  event.setMotd("§6La partie va très bientôt commencer. \n§7" + "Version" + " §8>> §7" + VERSION);
		    break;
		  case STARTED:
			  event.setMotd("§b§oLa partie a déjà commencé. \n§7" + "Version" + " §8>> §7" + VERSION);
			    break;
		  case PAUSE:
			  event.setMotd("§c§oLa partie est en pause. \n§7" + "Version" + " §8>> §7" + VERSION);
			    break;
		  default:
			  event.setMotd("§7§oChargement du plugin Fallen Kingdom.");
		}
	}
}
