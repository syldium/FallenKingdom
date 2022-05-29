package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RuleChangeListener implements Listener {

    private final Fk plugin;

    public RuleChangeListener(@NotNull Fk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public <T> void onRuleChange(RuleChangeEvent<T> event) {
        // Notifie la partie qu'il faut vérifier les caps après que la règle a changé
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getGame().onRuleChange(event));
    }
}
