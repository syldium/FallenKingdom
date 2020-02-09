package fr.devsylone.fallenkingdom.listeners.motd;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import fr.devsylone.fallenkingdom.Fk;


public class MotdListener implements Listener {

    @EventHandler
    public void countDown(final ServerListPingEvent event) {

        String serverVersion = (((Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]).replace("v", "")).replace("_", ".")).split(".R")[0];;

        switch (Fk.getInstance().getGame().getState()) {
            case BEFORE_STARTING:
                event.setMotd("§aLa partie n'a pas encore commencé. \n§7" + "Version" + " §8>> §7" + serverVersion);
                break;
            case STARTING:
                event.setMotd("§6La partie va très bientôt commencer. \n§7" + "Version" + " §8>> §7" + serverVersion);
                break;
            case STARTED:
                event.setMotd("§bLa partie a déjà commencé. \n§7" + "Version" + " §8>> §7" + serverVersion);
                break;
            case PAUSE:
                event.setMotd("§cLa partie est en pause. \n§7" + "Version" + " §8>> §7" + serverVersion);
                break;
            default:
                event.setMotd("§7§oChargement du plugin Fallen Kingdom.");
        }
    }
}