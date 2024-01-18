package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import fr.devsylone.fkpi.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PlayerColorUtil {

    /**
     * Set a player's color from the color of the team.
     */
    public static void setPlayerColorFromTeam(String p, Team t) {
        Player player = Bukkit.getPlayer(p);
        if (player != null) {
            setPlayerColorFromTeam(player, t);
        }

    }

    public static void setPlayerColorFromTeam(Player player, Team t) {
        player.displayName(Component
            .text(player.getName())
            .color(TextColor.color(t.getColor().getRGB())));
    }

    /**
     * Update player colors for each player in the team.
     */
    public static void updatePlayerColors(Team t) {
        t.getPlayers().forEach(p -> setPlayerColorFromTeam(p, t));
    }


    /**
     * Reset a player's display name to default display name.
     */
    public static void resetPlayerColor(String p) {
        Player player = Bukkit.getPlayer(p);
        if (player != null) {
            resetPlayerColor(player);
        }
    }

    public static void resetPlayerColor(Player player) {
        player.displayName(null);
    }

    /**
     * Reset player display names for all players in a team.
     */
    public static void resetPlayerColors(Team t) {
        t.getPlayers().forEach(p -> resetPlayerColor(p));
    }
}
