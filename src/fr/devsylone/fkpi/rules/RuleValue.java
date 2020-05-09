package fr.devsylone.fkpi.rules;

import fr.devsylone.fkpi.util.Saveable;

public interface RuleValue extends Saveable
{
    String format();

    default void fillWithDefaultValue()
    {
    }
}