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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BukkitAdvancement {

    private static Field BUKKIT_ADVANCEMENT;
    private static Field NMS_DISPLAY;
    private static Method GET_HANDLE;
    private static Method GET_PARENT;

    static {
        try {
            GET_HANDLE = NMSUtils.obcClass("advancement.CraftAdvancement").getMethod("getHandle");
            final Class<?> nmsAdvancement = GET_HANDLE.getReturnType();
            GET_PARENT = NMSUtils.getMethod(nmsAdvancement, nmsAdvancement);
            BUKKIT_ADVANCEMENT = NMSUtils.getField(nmsAdvancement, Advancement.class, f -> true);
            NMS_DISPLAY = NMSUtils.getField(nmsAdvancement, NMSUtils.nmsClass("advancements", "AdvancementDisplay"), field -> !Modifier.isStatic(field.getModifiers()));
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

            Class<?> craftItemStack = XItemStack.ITEM_STACK;
            Class<?> nmsItemStackClass = craftItemStack.getMethod("asNMSCopy", ItemStack.class).getReturnType();
            Field nmsItemStackField = Arrays.stream(display.getClass().getDeclaredFields())
                    .filter(f -> f.getType().equals(nmsItemStackClass))
                    .findAny().orElseThrow(RuntimeException::new);
            nmsItemStackField.setAccessible(true);
            Object nmsItemStack = nmsItemStackField.get(display);
            final ItemStack itemStack = (ItemStack) craftItemStack.getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, nmsItemStack);
            XItemStack.setDisplayNameComponents(name, itemStack);
            XItemStack.setLoreComponents(description, itemStack);
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
            return NMS_DISPLAY.get(GET_HANDLE.invoke(advancement));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}
