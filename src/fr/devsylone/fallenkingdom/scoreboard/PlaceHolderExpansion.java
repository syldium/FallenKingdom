package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PlaceHolderExpansion extends PlaceholderExpansion
{
    private final List<String> placeholders;

    public PlaceHolderExpansion() {
        this.placeholders = Arrays.stream(PlaceHolder.values())
                .map(placeholder -> '%' + getIdentifier() + '_' + placeholder.getRawKey().toLowerCase(Locale.ROOT) + '%')
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fk";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return Fk.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return this.placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (!player.isOnline())
            return "";
        for (PlaceHolder placeHolder : PlaceHolder.values()) {
            if (placeHolder.getRawKey().equalsIgnoreCase(identifier)) {
                return String.valueOf(placeHolder.getFunction().apply((Player) player,0));
            }
        }
        return null;
    }
}
