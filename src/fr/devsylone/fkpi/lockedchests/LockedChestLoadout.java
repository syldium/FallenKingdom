package fr.devsylone.fkpi.lockedchests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;

public class LockedChestLoadout implements Saveable {
    @Getter
    private List<ItemStack> inventory = new ArrayList<>();
    @Getter
    private int time;


    public static LockedChestLoadout from(ConfigurationSection config) {
        LockedChestLoadout loadout = new LockedChestLoadout();
        loadout.load(config);
        return loadout;
    }


    @Override
    public void load(ConfigurationSection config) {
        if (!config.contains("time") || !config.isInt("time")) {
            time = -1;
        }
        if (!config.contains("inventory") || !config.isConfigurationSection("inventory")) {
            return;
        }
        ConfigurationSection invConfig = config.getConfigurationSection("inventory");
        List<Integer> slots =
                invConfig.getKeys(false).stream().filter(key -> StringUtils.isNumeric(key))
                        .map(key -> Integer.parseInt(key)).collect(Collectors.toList());
        inventory = new ArrayList<>(slots.size());
        for (Integer i = 0; i < slots.size(); i++) {
            if (invConfig.isItemStack(i.toString())) {
                inventory.add(invConfig.getItemStack(i.toString()));
            }
        }

    }

    @Override
    public void save(ConfigurationSection config) {
        config.set("time", time);
        ConfigurationSection invConfig = config.createSection("inventory");
        for (Integer i = 0; i < inventory.size(); i++) {
            invConfig.set(i.toString(), inventory.get(i));
        }
    }
}
