package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class DerivedPotionIterator implements Iterator<XPotionData> {


    private final PotionType[] types;
    private int index = 0;
    private final List<XPotionData> variants = new ArrayList<>(2);
    private int variantsIndex = 0;

    DerivedPotionIterator(PotionType[] types) {
        this.types = types;
    }

    public boolean hasNext() {
        return this.index < this.types.length || this.variantsIndex < this.variants.size();
    }

    @Override
    public XPotionData next() {
        if (this.variantsIndex < this.variants.size()) {
            return this.variants.get(this.variantsIndex++);
        }
        final PotionType type = this.types[this.index++];
        this.variants.clear();
        this.variantsIndex = 0;
        if (XPotionData.isExtendable(type)) {
            this.variants.add(new XPotionData(type, true, false));
        }
        if (XPotionData.isUpgradable(type)) {
            this.variants.add(new XPotionData(type, false, true));
        }
        return new XPotionData(type, false, false);
    }
}
