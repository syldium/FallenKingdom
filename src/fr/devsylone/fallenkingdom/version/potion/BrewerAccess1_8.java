package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class BrewerAccess1_8 implements BrewerAccess {

    // 1.8 TileEntityBrewingStand#o(): void
    private static final Method POTION_BREW;

    // 1.8 TileEntityBrewingStand
    private static final Constructor<?> TILE_BREWING_STAND;
    private static final Method TILE_BREWING_STAND_SET_ITEM;
    private static final Method TILE_BREWING_STAND_GET_ITEM;

    static {
        try {
            Class<?> tileEntityBrewingStand = NMSUtils.nmsClass("world.level.block.entity", "TileEntityBrewingStand");
            TILE_BREWING_STAND = tileEntityBrewingStand.getConstructor();
            TILE_BREWING_STAND_SET_ITEM = tileEntityBrewingStand
                    .getMethod("setItem", int.class, XItemStack.ITEM_STACK);
            TILE_BREWING_STAND_GET_ITEM = tileEntityBrewingStand
                    .getMethod("getItem", int.class);
            POTION_BREW = tileEntityBrewingStand.getDeclaredMethod("o");
            POTION_BREW.setAccessible(true);
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public ItemStack mix(World world, ItemStack input, ItemStack ingredient) {
        try {
            Object fakeTileEntityStand = TILE_BREWING_STAND.newInstance();
            TILE_BREWING_STAND_SET_ITEM.invoke(fakeTileEntityStand, 0 /*first potion slot*/, XItemStack.asCraftItem(input));
            TILE_BREWING_STAND_SET_ITEM.invoke(fakeTileEntityStand, 3 /*ingredient slot*/, XItemStack.asCraftItem(ingredient));
            POTION_BREW.invoke(fakeTileEntityStand);
            return XItemStack.asBukkitItem(TILE_BREWING_STAND_GET_ITEM.invoke(fakeTileEntityStand, 0 /*first potion slot*/));
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to mix potion", ex);
        }
    }
}
