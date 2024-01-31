package fr.devsylone.fkpi.lockedchests;

import static fr.devsylone.fallenkingdom.utils.KeyHelper.parseKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import fr.devsylone.fallenkingdom.version.Version.VersionType;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;
import lombok.Setter;

public class LockedChestLoadout implements Saveable {
    @Getter
    private int time = -1;
    private List<ItemStack> inventory = new ArrayList<>();
    private String lootTable;
    @Getter
    @Setter
    private String advancement = new String();

    enum ChestKind {
        SET_INVENTORY, LOOT_TABLE
    }

    private ChestKind kind;

    /**
     * Getter for the chest's inventory. If the chest loadout is based off a loot table, returns an
     * inventory based on the loot table.
     *
     * @param {@link Location} of the chest.
     *
     * @return List of {@link ItemStack} as inventory.
     */
    public List<ItemStack> getInventory(@NotNull Location loc) {
        if (kind == ChestKind.LOOT_TABLE) {
            return new ArrayList<>(Bukkit.getLootTable(parseKey(lootTable))
                    .populateLoot(new Random(), new LootContext.Builder(loc).build()));
        }
        return inventory;
    }


    /**
     * Loads a new loadout object from a configuration.
     *
     * @param config {@link ConfigurationSection}
     * @return {@link LockedChestLoadout}
     */
    public static LockedChestLoadout from(ConfigurationSection config) {
        LockedChestLoadout loadout = new LockedChestLoadout();
        loadout.load(config);
        return loadout;
    }

    private LockedChestLoadout() {}

    public LockedChestLoadout(int unlockTime, int expiry, @Nullable String advancement,
            ItemStack[] inventory) {
        this.time = unlockTime * 1000;
        this.advancement = advancement == null ? new String() : advancement;
        this.inventory = Arrays.asList(inventory);
        kind = ChestKind.SET_INVENTORY;
    }


    // -- Saveable -- //


    @Override
    public void load(ConfigurationSection config) {
        if (config.isInt("time")) {
            time = config.getInt("time");
        }
        if (config.isString("Advancement")) {
            advancement = config.getString("Advancement");
        }
        if (!config.isString("Kind")) {
            kind = ChestKind.SET_INVENTORY;
        } else {
            kind = ChestKind.valueOf(config.getString("Kind"));
        }

        // Loot table
        if (!VersionType.V1_13.isHigherOrEqual()) {
            kind = ChestKind.SET_INVENTORY;
        }
        if (kind == ChestKind.LOOT_TABLE && config.isString("LootTable")) {
            lootTable = config.getString("LootTable");
            return;
        }

        // Inventory
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
        config.set("Advancement", advancement);
        config.set("Kind", kind.name());

        if (kind == ChestKind.SET_INVENTORY) {
            ConfigurationSection invConfig = config.createSection("inventory");
            for (Integer i = 0; i < inventory.size(); i++) {
                invConfig.set(i.toString(), inventory.get(i));
            }
        } else {
            config.set("LootTable", lootTable);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unlock Time: " + (time / 1000));
        if (advancement != null && !advancement.isEmpty()) {
            sb.append(",Required advancement: " + advancement);
        }
        switch (kind) {
            case SET_INVENTORY:
                sb.append(",Items: ");
                sb.append(inventory.toString());
                break;
            case LOOT_TABLE:
            sb.append(",Loot table: " + lootTable);
                break;
        }
        return sb.toString();
    }
}
