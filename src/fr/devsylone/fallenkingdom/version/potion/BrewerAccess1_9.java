package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

import static fr.devsylone.fallenkingdom.utils.PacketUtils.MINECRAFT_SERVER;

class BrewerAccess1_9 implements BrewerAccess {

    // 1.9-26.2 PotionBrewer#d(ItemStack, ItemStack): ItemStack
    private static final Method POTION_BREW;

    // 1.21-26.2 MinecraftServer#brewingRecipeRegistry(): BrewingRecipeRegistry
    private static final Method GET_BREWING_REGISTRY;

    static {
        try {
            Class<?> brewingRegistry = NMSUtils.nmsClass("world.item.alchemy", "PotionBrewing", "PotionBrewer");
            POTION_BREW = NMSUtils.getMethod(brewingRegistry, XItemStack.ITEM_STACK, XItemStack.ITEM_STACK, XItemStack.ITEM_STACK);
            Method getBrewingRegistry = null;
            try {
                getBrewingRegistry = NMSUtils.getMethod(MINECRAFT_SERVER, brewingRegistry); // 1.21+
            } catch (NoSuchMethodException ignored) {}
            GET_BREWING_REGISTRY = getBrewingRegistry;
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public ItemStack mix(World world, ItemStack input, ItemStack ingredient) {
        try {
            Object registry = GET_BREWING_REGISTRY == null ? null : GET_BREWING_REGISTRY.invoke(PacketUtils.getNMSServer());
            Object craftPotion = XItemStack.asCraftItem(input);
            Object craftIngredient = XItemStack.asCraftItem(ingredient);
            return XItemStack.asBukkitItem(POTION_BREW.invoke(registry, craftIngredient, craftPotion));
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to mix potion", ex);
        }
    }
}
