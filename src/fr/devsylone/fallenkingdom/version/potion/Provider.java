package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fallenkingdom.version.Version.VersionType;

final class Provider {

    static BrewerAccess createInstance() {
        if (VersionType.V26_3.isHigherOrEqual()) {
            return new BrewerAccess26_3();
        } else if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return new BrewerAccess1_9();
        } else {
            return new BrewerAccess1_8();
        }
    }
}
