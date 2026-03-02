package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.TranslationArgument.component;

/**
 * Classe séparée pour éviter que des versions qui ne supportent
 * pas adventure ne chargent des classes inexistantes.
 */
class AdventureFormat {

    static void setDeathMessage(PlayerDeathEvent event, Team playerTeam, Team killerTeam) {
        Component deathMessage = event.deathMessage();
        if (deathMessage == null) return;
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(Messages.PREFIX_FK.getMessage());
        deathMessage = deathMessage.color(NamedTextColor.GRAY);
        if (deathMessage instanceof TranslatableComponent) {
            List<TranslationArgument> args = new ArrayList<>(((TranslatableComponent) deathMessage).arguments());
            if (playerTeam != null) {
                args.set(0, component(event.getEntity().displayName().color(TextColor.color(playerTeam.getColor().getRGB()))));
            }
            if (args.size() > 1 && event.getEntity().getKiller() != null && killerTeam != null) {
                args.set(1, component(event.getEntity().getKiller().displayName().color(TextColor.color(killerTeam.getColor().getRGB()))));
            }
            event.deathMessage(prefix.append(((TranslatableComponent) deathMessage).arguments(args)));
        } else {
            event.deathMessage(prefix.append(deathMessage));
        }
    }
}
