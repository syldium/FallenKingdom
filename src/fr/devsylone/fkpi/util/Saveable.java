package fr.devsylone.fkpi.util;

import org.bukkit.configuration.ConfigurationSection;

public interface Saveable
{	
    void load(ConfigurationSection config);

    default void loadNullable(ConfigurationSection config)
    {
        // Par défaut, ne charger que si la configuration existe et contient des éléments
        if (config != null && config.getKeys(true).size() > 0) {
            this.load(config);
        }
    }

    void save(ConfigurationSection config);
}