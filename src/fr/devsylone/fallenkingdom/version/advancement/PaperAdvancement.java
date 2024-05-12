package fr.devsylone.fallenkingdom.version.advancement;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.Style.style;

public final class PaperAdvancement {

    private static final Style GRAY_ITALIC = notItalic(NamedTextColor.GRAY);
    private static final Style BLUE_DARK_AQUA = notItalic(NamedTextColor.DARK_AQUA);
    private static final Style BLUE_ITALIC = notItalic(NamedTextColor.BLUE);

    public static @NotNull ItemStack asItemStack(@NotNull Key key, @NotNull AdvancementDisplay display) {
        final ItemStack itemStack = display.icon();
        final ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(display.title().style(BLUE_DARK_AQUA));
        meta.lore(Arrays.asList(text(key.asString(), GRAY_ITALIC), display.description().style(BLUE_ITALIC)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Contract("_ -> new")
    private static @NotNull Style notItalic(@NotNull TextColor color) {
        return style().color(color).decoration(TextDecoration.ITALIC, false).build();
    }

    public static void buildAdvancements(@NotNull List<ItemStack> categories, @NotNull Map<String, List<ItemStack>> representations) {
        final Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            final Advancement advancement = iterator.next();
            final AdvancementDisplay display = advancement.getDisplay();
            if (display == null) {
                continue;
            }

            final Advancement root = advancement.getRoot();
            final ItemStack icon = asItemStack(advancement.getKey(), display);
            if (advancement.getKey().equals(root.getKey())) {
                categories.add(icon);
            } else {
                representations.computeIfAbsent(root.getKey().toString(), s -> new ArrayList<>()).add(icon);
            }
        }
    }
}
