package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.stream.Stream;

public enum XMaterial {
    OAK_FENCE("FENCE"),
    PLAYER_HEAD((byte) 3, "SKULL_ITEM"),
    WHITE_WOOL("WOOL"),
    CYAN_STAINED_GLASS_PANE((byte) 9, "STAINED_GLASS_PANE"),
    END_PORTAL_FRAME("ENDER_PORTAL_FRAME"),
    GRASS_BLOCK("GRASS");

    private final Material material;
    private final byte data;

    XMaterial(String... olderNames) {
        this((byte) 0, olderNames);
    }

    XMaterial(byte data, String... olderNames) {
        String name = this.name();
        Stream<String> names = Stream.concat(Stream.of(name), Stream.of(olderNames));
        this.material = names.map(Material::matchMaterial).filter(Objects::nonNull).findFirst().orElse(null);
        this.data = data;
    }

    public Material material() {
        return this.material;
    }

    public ItemStack item() {
        return new ItemStack(this.material, 1, this.data);
    }
}
