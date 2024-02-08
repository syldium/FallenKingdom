package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.Iterator;

public final class PotionIterator {

    public static final boolean USE_SEPARATE_POTION_TYPES;

    static {
        boolean separatePotionTypes = false;
        try {
            PotionMeta.class.getMethod("setBasePotionType", PotionType.class);
            separatePotionTypes = true;
        } catch (NoSuchMethodException ignored) {
        }
        USE_SEPARATE_POTION_TYPES = separatePotionTypes;
    }

    private PotionIterator() {}

    public static Iterator<XPotionData> create(PotionType[] types) {
        return USE_SEPARATE_POTION_TYPES ? new SeparatePotionIterator(types) : new DerivedPotionIterator(types);
    }
}
