package fr.devsylone.fallenkingdom.version.potion;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import fr.devsylone.fallenkingdom.version.tracker.InternalRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import static fr.devsylone.fallenkingdom.utils.PacketUtils.MINECRAFT_SERVER;
import static fr.devsylone.fallenkingdom.utils.PacketUtils.getNMSWorld;
import static fr.devsylone.fallenkingdom.utils.XItemStack.asBukkitItem;
import static fr.devsylone.fallenkingdom.utils.XItemStack.asCraftItem;
import static fr.devsylone.fallenkingdom.version.tracker.MinecraftKey.RESOURCE;

class BrewerAccess26_3 implements BrewerAccess {

    // MinecraftServer#getRecipeManager(): RecipeManager
    private static final Method GET_RECIPE_MANAGER;

    // new BrewingInput(ItemStack, ItemStack)
    private static final Constructor<?> BREWING_INPUT;

    // RecipeManager#getRecipeFor<I, T extends Recipe<T>>(RecipeType<T>, I, Level, ResourceKey): Optional
    private static final Method GET_RECIPE_FOR;

    // RecipeType.BREWING
    private static final Object RECIPE_TYPE_BREWING;

    // RecipeHolder<T extends Recipe>#value(): T
    private static final Method RECIPE_HOLDER_VALUE;

    // BrewingRecipe#assemble(BrewingInput): ItemStack
    private static final Method BREWING_RECIPE_ASSEMBLE;

    static {
        try {
            Class<?> brewingInputClass = NMSUtils.nmsClass("world.item.crafting", "BrewingInput");
            Class<?> recipeManagerClass = NMSUtils.nmsClass("world.item.crafting", "RecipeManager");
            GET_RECIPE_MANAGER = NMSUtils.getMethod(MINECRAFT_SERVER, recipeManagerClass);
            BREWING_INPUT = brewingInputClass.getConstructor(XItemStack.ITEM_STACK, XItemStack.ITEM_STACK);
            Class<?> recipeTypeClass = NMSUtils.nmsClass("world.item.crafting", "RecipeType");
            Class<?> levelClass = NMSUtils.nmsClass("world.level", "Level");
            GET_RECIPE_FOR = recipeManagerClass.getMethod("getRecipeFor", recipeTypeClass, brewingInputClass.getInterfaces()[0], levelClass, RESOURCE);
            RECIPE_TYPE_BREWING = new InternalRegistry<>(NamespacedKey.minecraft("recipe_type")).get(NamespacedKey.minecraft("brewing"));
            RECIPE_HOLDER_VALUE = NMSUtils.nmsClass("world.item.crafting", "RecipeHolder").getMethod("value");
            BREWING_RECIPE_ASSEMBLE = NMSUtils.nmsClass("world.item.crafting", "BrewingRecipe").getMethod("assemble", brewingInputClass);
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }


    @Override
    public ItemStack mix(World world, ItemStack input, ItemStack ingredient) {
        try {
            Object recipeManager = GET_RECIPE_MANAGER.invoke(PacketUtils.getNMSServer());
            Object brewingInput = BREWING_INPUT.newInstance(asCraftItem(input), asCraftItem(ingredient));
            Optional<?> recipe = (Optional<?>) GET_RECIPE_FOR.invoke(recipeManager, RECIPE_TYPE_BREWING, brewingInput, getNMSWorld(world), null);
            if (recipe.isPresent()) {
                Object brewingRecipe = RECIPE_HOLDER_VALUE.invoke(recipe.get());
                return asBukkitItem(BREWING_RECIPE_ASSEMBLE.invoke(brewingRecipe, brewingInput));
            } else {
                return input;
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to mix potion", ex);
        }
    }
}
