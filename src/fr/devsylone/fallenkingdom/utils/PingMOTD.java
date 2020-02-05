package fr.devsylone.fallenkingdom.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;


public class PingMOTD implements Listener {
	
	@EventHandler
    public void countDown(final ServerListPingEvent event){
		if(Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING) {
		event.setMotd("§aLe Fallen King n'a pas encore commencé !\n§6Versions : 1.8 - 1.14.4");
		}
		else if(Fk.getInstance().getGame().getState() == GameState.STARTED) {
		event.setMotd("§6La partie est en cours. \n§6Versions : 1.8 - 1.14.4");
		}
		else if(Fk.getInstance().getGame().getState() == GameState.PAUSE) {
		event.setMotd("§cLa partie est en pause. \n§6Versions : 1.8 - 1.14.4");
		}
	}
}
