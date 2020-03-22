### Api Fk
Le plugin est doté de quelques évènements utilisables comme des évènements Spigot classiques :

```java
class FkListener implements Listener
{
	@EventHandler
	public void onDayEvent(final DayEvent event) { // Lié au changement de jour
		event.getDay(); //retourne un int indiquant le jour actuel
		event.getType(); //retourne le type d'évenement
	}
    @EventHandler
	public void onGameEvent(final GameEvent event) { // Début, pause ou reprise d'une partie
		event.getType(); //retourne le type d'évenement
	}
    @EventHandler
	public void onCaptureEvent(final TeamCaptureEvent event) { // Lors d'une capture de salle des coffres
		if (event.isSuccess()) {
		    Bukkit.broadcastMessage("Félicitations à l'équipe " + event.getAssailantsTeam().toString() + ChatColor.RESET + " !");
		}
	}
}
```

Les méthodes destinées à l'api externe sont situées dans le package [api](../../src/fr/devsylone/fkpi/api).
