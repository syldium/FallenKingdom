package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class TeleportTask implements Runnable {

    private final Team team;

    public TeleportTask(Team team) {
        this.team = team;
    }

    @Override
    public void run() {
        if (team.getBase() == null) {
            return;
        }

        for (String playerName : team.getPlayers()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                return;
            }

            player.teleport(team.getBase().getTpPoint());

            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 4));
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFlying(false);
            Fk.getInstance().getStarterInventoryManager().applyStarterInv(player);
            player.playSound(player.getLocation(), FkSound.EXPLODE.bukkitSound(), 1, 1);
        }
    }
}
