package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe séparée pour éviter que des versions qui ne supportent
 * pas adventure ne chargent des classes inexistantes.
 */
class AdventureFormat {

    static void setDeathMessage(PlayerDeathEvent event, Team playerTeam, Team killerTeam) {
        Component deathMessage = event.deathMessage();
        if (deathMessage == null) return;
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(ChatUtils.PREFIX);
        deathMessage = deathMessage.color(NamedTextColor.GRAY);
        if (deathMessage instanceof TranslatableComponent) {
            List<Component> args = new ArrayList<>(((TranslatableComponent) deathMessage).args());
            if (playerTeam != null) {
                args.set(0, event.getEntity().displayName().color(TextColor.color(playerTeam.getColor().getRGB())));
            }
            if (args.size() > 1 && event.getEntity().getKiller() != null && killerTeam != null) {
                args.set(1, event.getEntity().getKiller().displayName().color(TextColor.color(killerTeam.getColor().getRGB())));
            }
            event.deathMessage(prefix.append(((TranslatableComponent) deathMessage).args(args)));
        } else {
            event.deathMessage(prefix.append(deathMessage));
        }
    }
}
