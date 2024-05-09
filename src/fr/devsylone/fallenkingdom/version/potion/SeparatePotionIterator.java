package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.potion.PotionType;

import java.util.Iterator;

class SeparatePotionIterator implements Iterator<XPotionData> {

    private final PotionType[] types;
    private int index = 0;

    SeparatePotionIterator(PotionType[] types) {
        this.types = types;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.types.length;
    }

    @Override
    public XPotionData next() {
        final PotionType type = this.types[this.index++];
        return XPotionData.fromModernPotionType(type);
    }
}
