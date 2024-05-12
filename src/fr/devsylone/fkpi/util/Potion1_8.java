package fr.devsylone.fkpi.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

/**
 * A stub for the {@code org.bukkit.potion.Potion} class that should not be used after 1.8.
 */
@Deprecated
class Potion1_8 {

    Potion1_8(PotionType type, int level, boolean splash, boolean extended) {
        throw new IllegalStateException("not injected");
    }

    static Potion1_8 fromItemStack(ItemStack item) {
        throw new IllegalStateException("not injected");
    }

    PotionType getType() {
        throw new IllegalStateException("not injected");
    }

    int getLevel() {
        throw new IllegalStateException("not injected");
    }

    boolean hasExtendedDuration() {
        throw new IllegalStateException("not injected");
    }

    void apply(ItemStack to) {
        throw new IllegalStateException("not injected");
    }
}
