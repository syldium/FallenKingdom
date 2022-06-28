package fr.devsylone.fallenkingdom.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static fr.devsylone.fallenkingdom.utils.KeyHelper.parseKey;

@SuppressWarnings({"rawtypes", "unchecked"})
public class XAdvancement {

    private static final Class<Enum> ACHIEVEMENT;
    private static final Method HAS_ACHIEVEMENT;

    static {
        Optional<Class<?>> achievement = NMSUtils.optionalClass("org.bukkit.Achievement");
        if (achievement.isPresent() && achievement.get().getAnnotations().length < 1) {
            ACHIEVEMENT = (Class<Enum>) achievement.get();
            try {
                HAS_ACHIEVEMENT = Player.class.getDeclaredMethod("hasAchievement", ACHIEVEMENT);
            } catch (NoSuchMethodException e) {
                throw new ExceptionInInitializerError(e);
            }
        } else {
            ACHIEVEMENT = null;
            HAS_ACHIEVEMENT = null;
        }
    }

    /**
     * Retourne si la version du serveur courant supporte les succès (depuis la 1.12).
     *
     * @return Si les succès sont supportés
     */
    public static boolean isAdvancement() {
        return ACHIEVEMENT == null;
    }

    public static Object[] getAchievements() {
        return Objects.requireNonNull(ACHIEVEMENT, "Unable to get achievements since 1.12.").getEnumConstants();
    }

    /**
     * Vérifie que le nom donné peut être associé à un succès/trophée indépendamment de la version du serveur.
     *
     * @param name Nom à tester
     * @return Si l'association a pu se faire
     */
    public static boolean exist(String name) {
        try {
            if (isAdvancement()) {
                return Bukkit.getServer().getAdvancement(parseKey(name)) != null;
            }
            Enum.valueOf(ACHIEVEMENT, name.toUpperCase());
            return true;
        } catch (IllegalArgumentException ignored) {

        }
        return false;
    }

    /**
     * Vérifie si le joueur a le succès donné
     *
     * @param player Joueur à tester
     * @param name Nom du succès
     * @return Si le joueur a le succès
     */
    public static boolean hasAdvancement(Player player, String name) {
        if (isAdvancement()) {
            Advancement advancement = Bukkit.getAdvancement(parseKey(name));
            Objects.requireNonNull(advancement, "The success " + name + " does not exist.");
            return player.getAdvancementProgress(advancement).isDone();
        }
        Object achievement = Enum.valueOf(ACHIEVEMENT, name.toUpperCase());
        try {
            return (boolean) HAS_ACHIEVEMENT.invoke(player, achievement);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Retourne un itérateur des noms des succès
     *
     * @return Itérateur
     */
    public static Iterator<String> iterator() {
        return ACHIEVEMENT == null ? new AdvancementIterator(Bukkit.getServer().advancementIterator()) : new AchievementIterator(ACHIEVEMENT);
    }

    /**
     * Créé un ItemStack représentant le trophée donné (plus ou moins)
     *
     * @param achievement Trophée
     * @return Représentation du trophée
     */
    public static ItemStack getAchievementIcon(Object achievement) {
        String name = achievement.toString().toLowerCase().replace("_", " ");
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        for (String word : ChatColor.stripColor(name).toLowerCase().split(" ")) {
            if (word.charAt(word.length() - 1) == 's') {
                word = word.substring(0, word.length() - 1);
            }
            if (word.length() < 3) {
                continue;
            }
            for (Material material : Material.values()) {
                for (String n : material.name().split("_")) {
                    if (n.equalsIgnoreCase(word)) {
                        return build(material, name, achievement.toString());
                    }
                }
            }
            for (EntityType entityType : EntityType.values()) {
                if (entityType.name().equalsIgnoreCase(word)) {
                    ItemStack skull = new ItemStack(XMaterial.PLAYER_HEAD.parseItem());
                    SkullMeta skullMeta = SkullUtils.applySkin(skull.getItemMeta(), "MHF_" + word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1));
                    skull.setItemMeta(skullMeta);
                    return build(skull, name, achievement.toString());
                }
            }
        }
        return build(Material.GOLD_INGOT, name, achievement.toString());
    }

    private static ItemStack build(Material material, String displayName, String lore) {
        return build(new ItemStack(material), displayName, lore);
    }

    private static ItemStack build(ItemStack itemStack, String displayName, String lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + displayName);
        meta.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    static class AdvancementIterator implements Iterator<String> {

        private final Iterator<Advancement> iterator;

        AdvancementIterator(Iterator<Advancement> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public String next() {
            return iterator.next().getKey().toString();
        }
    }

    static class AchievementIterator implements Iterator<String> {

        private final Class<Enum> achievementEnum;
        private int pos = 0;

        AchievementIterator(Class<Enum> achievementEnum) {
            this.achievementEnum = achievementEnum;
        }

        @Override
        public boolean hasNext() {
            return pos < achievementEnum.getEnumConstants().length;
        }

        @Override
        public String next() {
            return achievementEnum.getEnumConstants()[pos++].name().toLowerCase();
        }
    }
}
