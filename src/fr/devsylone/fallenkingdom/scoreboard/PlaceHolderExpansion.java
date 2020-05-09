package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceHolderExpansion extends PlaceholderExpansion
{
    @Override
    public String getIdentifier() {
        return "fk";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor(){
        return Fk.getInstance().getDescription().getAuthors().toString();
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        if (!player.isOnline())
            return "";
        for (PlaceHolder placeHolder : PlaceHolder.values()) {
            if (placeHolder.getShortestKey().equalsIgnoreCase(identifier)) {
                return String.valueOf(placeHolder.getFunction().apply((Player) player,0));
            }
        }
        return null;
    }
}
