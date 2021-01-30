package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe séparée pour éviter que des versions qui ne supportent
 * pas adventure ne chargent des classes inexistantes.
 *
 * Utilisation de la réflection pour y accéder, pour faciliter le build du projet.
 */
class AdventureFormat {

    private static final Field DEATH_MESSAGE_FIELD;

    static {
        try {
            DEATH_MESSAGE_FIELD = PlayerDeathEvent.class.getDeclaredField("deathMessage");
            DEATH_MESSAGE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static void setDeathMessage(PlayerDeathEvent event, Team playerTeam, Team killerTeam) throws ReflectiveOperationException {
        Component deathMessage = (Component) DEATH_MESSAGE_FIELD.get(event);
        if (deathMessage == null) return;
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(ChatUtils.PREFIX);
        deathMessage = deathMessage.color(NamedTextColor.GRAY);
        if (deathMessage instanceof TranslatableComponent) {
            List<Component> args = new LinkedList<>();
            if (playerTeam != null) {
                args.add(Component.text(event.getEntity().getName(), TextColor.color(playerTeam.getColor().getRGB())));
            }
            if (event.getEntity().getKiller() != null && killerTeam != null) {
                args.add(Component.text(event.getEntity().getKiller().getName(), TextColor.color(killerTeam.getColor().getRGB())));
            }
            DEATH_MESSAGE_FIELD.set(event, prefix.append(((TranslatableComponent) deathMessage).args(args)));
        } else {
            DEATH_MESSAGE_FIELD.set(event, prefix.append(deathMessage));
        }
    }
}
