package fr.devsylone.fallenkingdom.version.advancement;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BukkitAdvancement {

    private static Field BUKKIT_ADVANCEMENT;
    private static Method GET_DISPLAY;
    private static Method GET_HANDLE;
    private static Method GET_PARENT;
    private static Method GET_ICON;
    private static Method GET_BUKKIT_ITEM_STACK;

    static {
        try {
            GET_HANDLE = NMSUtils.obcClass("advancement.CraftAdvancement").getMethod("getHandle");
            final Class<?> nmsAdvancement = GET_HANDLE.getReturnType();
            GET_PARENT = NMSUtils.getMethod(nmsAdvancement, nmsAdvancement);
            BUKKIT_ADVANCEMENT = NMSUtils.getField(nmsAdvancement, Advancement.class, f -> true);
            final Class<?> nmsDisplay = NMSUtils.nmsClass("advancements", "AdvancementDisplay");
            GET_DISPLAY = NMSUtils.getMethod(nmsAdvancement, nmsDisplay);
            final Class<?> craftItemStack = NMSUtils.obcClass("inventory.CraftItemStack");
            final Class<?> nmsItemStackClass = craftItemStack.getMethod("asNMSCopy", ItemStack.class).getReturnType();
            GET_ICON = NMSUtils.getMethod(nmsDisplay, nmsItemStackClass);
            GET_BUKKIT_ITEM_STACK = craftItemStack.getMethod("asBukkitCopy", nmsItemStackClass);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private static @NotNull ItemStack createIcon(@NotNull String key, @NotNull Object display) {
        try {
            BaseComponent[] name = XItemStack.getTextComponent(display, 0);
            for (BaseComponent component : name) {
                component.setItalic(false);
                component.setColor(ChatColor.RESET);
            }
            List<BaseComponent[]> description = new ArrayList<>();
            TextComponent namespacedKey = new TextComponent(key);
            namespacedKey.setItalic(false);
            namespacedKey.setColor(ChatColor.GRAY);
            description.add(new BaseComponent[]{namespacedKey});

            BaseComponent[] desc = XItemStack.getTextComponent(display, 1);
            if (new TextComponent(desc).toPlainText().length() < 32) {
                description.add(desc);
                for (BaseComponent component : description.get(1)) {
                    component.setItalic(false);
                    component.setColor(ChatColor.GREEN);
                }
            }

            Object nmsItemStack = GET_ICON.invoke(display);
            final ItemStack itemStack = (ItemStack) GET_BUKKIT_ITEM_STACK.invoke(null, nmsItemStack);
            XItemStack.setDisplayNameAndLore(itemStack, name, description);
            return itemStack;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void buildAdvancements(@NotNull List<ItemStack> categories, @NotNull Map<String, List<ItemStack>> representations) {
        final Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            final Advancement advancement = iterator.next();
            final Object display = getDisplay(advancement);
            if (display == null) {
                continue;
            }

            final Advancement root = getRoot(advancement);
            final ItemStack icon = createIcon(advancement.getKey().toString(), display);
            if (advancement == root) {
                categories.add(icon);
            } else {
                representations.computeIfAbsent(root.getKey().toString(), s -> new ArrayList<>()).add(icon);
            }
        }
    }

    private static @NotNull Advancement getRoot(@NotNull Advancement advancement) {
        try {
            Object nms = GET_HANDLE.invoke(advancement);
            while (GET_PARENT.invoke(nms) != null) {
                nms = GET_PARENT.invoke(nms);
            }
            return (Advancement) BUKKIT_ADVANCEMENT.get(nms);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static @Nullable Object getDisplay(@NotNull Advancement advancement) {
        try {
            return GET_DISPLAY.invoke(GET_HANDLE.invoke(advancement));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}
