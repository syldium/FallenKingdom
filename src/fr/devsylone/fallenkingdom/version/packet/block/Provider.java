package fr.devsylone.fallenkingdom.version.packet.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

final class Provider {

    static final BlockChange BLOCK_CHANGE;

    static {
        boolean api = false;
        try {
            Player.class.getMethod("sendBlockChange", Location.class, Material.class, byte.class);
            api = true;
        } catch (ReflectiveOperationException ignored) {}
        BLOCK_CHANGE = api ? new BukkitBlockChange() : new NMSBlockChange();
    }
}
