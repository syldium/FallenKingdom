### Api Fk
Le plugin est doté de quelques évènements utilisables comme des évènements Spigot classiques.
Exemple d'utilisation :
```java
class FkListener implements Listener {

    @EventHandler
    public void onTeamChange(final PlayerTeamChangeEvent event) { // Lors d'un changement d'équipe
        Player player = Bukkit.getPlayer(event.getPlayerName());
        if (event.getTeam() == null || player == null) {
            return;
        }
        // Le code suivant utilise les Material introduits en 1.13
        DyeColor dyeColor = event.getTeam().getDyeColor();
        ItemStack banner = new ItemStack(Material.getMaterial(dyeColor.name() + "_BANNER"));
        player.getInventory().setHelmet(banner);
    }
    @EventHandler
    public void onChestChangeState(final PlayerLockedChestInteractEvent event) { // Lors d'une capture de salle des coffres
        ITeam team = FkPI.getInstance().getTeamManager().getPlayerTeam(event.getPlayer());
        if (team == null) {
            // Annulation de la tentative de crochetage si le joueur n'a pas d'équipe
            event.setCancelled(true);
        }
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