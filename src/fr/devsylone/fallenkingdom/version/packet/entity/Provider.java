package fr.devsylone.fallenkingdom.version.packet.entity;

import fr.devsylone.fallenkingdom.version.Version;

final class Provider {

    static final Hologram HOLOGRAM;
    static final BossBar.Factory BOSS_BAR;

    private static Hologram initVersion() {
        switch (Version.VERSION_TYPE) {
            case V1_8:
                return new NMSHologram1_8();
            case V1_9_V1_12:
                return new NMSHologram1_9();
            case V1_13:
                return new NMSHologram1_13();
            case V1_14_V1_15:
                return new NMSHologram1_14();
            case V1_16:
                return new NMSHologram1_16();
            case V1_17:
            case V1_19:
            case V1_20:
                return new NMSHologram1_17();
            default:
                throw new RuntimeException("Could not get packet manager by version!");
        }
    }

    static {
        Hologram hologram;
        try {
            hologram = initVersion();
        } catch (Throwable throwable) {
            hologram = Version.VersionType.V1_20.isHigherOrEqual() ? new DisplayBukkitHologram() : new BukkitHolograms();
        }
        HOLOGRAM = hologram;
        if (HOLOGRAM instanceof NMSHologram1_8) {
            BOSS_BAR = (name, color) -> new WitherBossBar1_8(name);
        } else {
            BOSS_BAR = BukkitBossBar::new;
        }
    }
}
