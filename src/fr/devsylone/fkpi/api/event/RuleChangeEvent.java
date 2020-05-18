package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Déclenché lors d'un changement de règle.
 *
 * Noter que le changement n'a pas encore été répercuté.
 * @param <T> Type de la valeur de la règle
 */
public class RuleChangeEvent<T> extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final Rule<T> rule;
    private final T value;

    public RuleChangeEvent(Rule<T> rule, T value, boolean isAsync)
    {
        super(isAsync);
        this.rule = rule;
        this.value = value;
    }

    public RuleChangeEvent(Rule<T> rule, T newValue)
    {
        this(rule, newValue, !Bukkit.isPrimaryThread());
    }

    public Rule<T> getRule()
    {
        return rule;
    }

    public T getValue()
    {
        return value;
    }

    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
