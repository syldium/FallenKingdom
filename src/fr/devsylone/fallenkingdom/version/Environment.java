package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fr.devsylone.fallenkingdom.version.Version.classExists;

public class Environment {

    private static final boolean HAS_ASYNC_TELEPORT;
    private static final boolean HAS_MULTI_BLOCK_CHANGE;
    private static final boolean HAS_UUID_BY_PLAYER_NAME;
    private static final boolean HAS_ASYNC_CHUNK_LOAD;
    private static final boolean HAS_ADVENTURE_API;
    private static final boolean HAS_MIN_HEIGHT;
    private static final boolean HAS_SPIGOT_BOOK_PAGES;
    private static final boolean HAS_DIRECT_INVENTORY_HOLDER;
    private static final boolean HAS_ENCHANTMENT_GLINT_OVERRIDE;
    private static final boolean HAS_ENTITY_BY_UUID;
    private static final boolean HAS_GAME_RULE_REGISTRY_PAPER;
    private static final boolean HAS_GAME_RULE_REGISTRY_SPIGOT;
    public static final boolean HAS_DATA_COMPONENTS;

    static {
        boolean hasAsyncTeleport = false;
        try {
            Entity.class.getMethod("teleportAsync", Location.class);
            hasAsyncTeleport = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_ASYNC_TELEPORT = hasAsyncTeleport;

        boolean hasMultiBlockChange = false;
        try {
            Player.class.getDeclaredMethod("sendMultiBlockChange", java.util.Map.class);
            hasMultiBlockChange = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_MULTI_BLOCK_CHANGE = hasMultiBlockChange;

        boolean hasUuidByPlayerName = false;
        try {
            Bukkit.class.getMethod("getPlayerUniqueId", String.class);
            hasUuidByPlayerName = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_UUID_BY_PLAYER_NAME = hasUuidByPlayerName;

        boolean hasAsyncChunkLoad = false;
        try {
            World.class.getMethod("getChunkAtAsync", int.class, int.class);
            hasAsyncChunkLoad = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_ASYNC_CHUNK_LOAD = hasAsyncChunkLoad;

        boolean hasAdventureApi = false;
        try {
            Class<?> component = Class.forName("net.kyori.adventure.text.Component");
            PlayerDeathEvent.class.getMethod("deathMessage", component);
            hasAdventureApi = true;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) { }
        HAS_ADVENTURE_API = hasAdventureApi;

        boolean hasMinHeight = false;
        try {
            World.class.getMethod("getMinHeight");
            hasMinHeight = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_MIN_HEIGHT = hasMinHeight;

        boolean spigotPages = false;
        try {
            Class.forName(BookMeta.class.getName() + "$Spigot").getMethod("setPages", List.class);
            spigotPages = true;
        } catch (ReflectiveOperationException ignored) { }
        HAS_SPIGOT_BOOK_PAGES = spigotPages;

        boolean hasDirectInventoryHolder = false;
        try {
            Inventory.class.getMethod("getHolder", boolean.class);
            hasDirectInventoryHolder = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_DIRECT_INVENTORY_HOLDER = hasDirectInventoryHolder;

        boolean hasEnchantmentGlintOverride = false;
        try {
            ItemMeta.class.getMethod("setEnchantmentGlintOverride", Boolean.class);
            hasEnchantmentGlintOverride = true;
        } catch (ReflectiveOperationException ignored) { }
        HAS_ENCHANTMENT_GLINT_OVERRIDE = hasEnchantmentGlintOverride;

        boolean hasEntityByUuid = false;
        try {
            World.class.getMethod("getEntity", UUID.class);
            hasEntityByUuid = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_ENTITY_BY_UUID = hasEntityByUuid;

        boolean hasDataComponents = false;
        try {
            Class<?> valuedClass = Class.forName("io.papermc.paper.datacomponent.DataComponentType$Valued");
            ItemStack.class.getMethod("setData", valuedClass, Object.class);
            hasDataComponents = true;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) { }
        HAS_DATA_COMPONENTS = hasDataComponents;

        HAS_GAME_RULE_REGISTRY_PAPER = classExists("org.bukkit.GameRules");
        boolean hasGameRuleRegistrySpigot = false;
        try {
            Class.forName("org.bukkit.Registry").getField("GAME_RULE");
            hasGameRuleRegistrySpigot = true;
        } catch (ClassNotFoundException | NoSuchFieldException ignored) { }
        HAS_GAME_RULE_REGISTRY_SPIGOT = hasGameRuleRegistrySpigot;
    }

    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        if (HAS_ASYNC_TELEPORT) {
            return entity.teleportAsync(location);
        }
        return CompletableFuture.completedFuture(entity.teleport(location)); // Sinon synchrone
    }

    public static UUID getPlayerUniqueId(String playerName) {
        if (HAS_UUID_BY_PLAYER_NAME) {
            return Bukkit.getPlayerUniqueId(playerName);
        }
        Player player = Bukkit.getPlayerExact(playerName);
        return player == null ? null : player.getUniqueId();
    }

    public static CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z) {
        if (HAS_ASYNC_CHUNK_LOAD) {
            return world.getChunkAtAsync(x, z);
        }
        return CompletableFuture.completedFuture(world.getChunkAt(x, z));
    }

    public static void setDeathMessage(PlayerDeathEvent event, Team playerTeam, Team killerTeam) {
        if (HAS_ADVENTURE_API) {
            AdventureFormat.setDeathMessage(event, playerTeam, killerTeam);
            return;
        }

        String deathMessage = event.getDeathMessage();
        if (deathMessage == null) return;
        deathMessage = Messages.PREFIX_FK.getMessage() + deathMessage;
        if (playerTeam != null) {
            deathMessage = deathMessage.replace(event.getEntity().getName(), event.getEntity().getDisplayName() + ChatColor.GRAY);
        }
        if (event.getEntity().getKiller() != null && killerTeam != null) {
            deathMessage = deathMessage.replace(event.getEntity().getKiller().getName(), event.getEntity().getKiller().getDisplayName() + ChatColor.GRAY);
        }
        event.setDeathMessage(deathMessage);
    }

    public static int getMinHeight(World world) {
        return HAS_MIN_HEIGHT ? world.getMinHeight() : 0;
    }

    public static boolean hasMultiBlockChange() {
        return HAS_MULTI_BLOCK_CHANGE;
    }

    public static boolean hasSpigotBookPages() {
        return HAS_SPIGOT_BOOK_PAGES;
    }

    public static @Nullable InventoryHolder getInventoryHolder(@NotNull Inventory inventory) {
        if (HAS_DIRECT_INVENTORY_HOLDER) {
            return inventory.getHolder(false);
        }
        return inventory.getHolder();
    }

    public static void setEnchantmentGlintOverride(@NotNull ItemMeta itemMeta, boolean overrideGlint) {
        if (HAS_ENCHANTMENT_GLINT_OVERRIDE) {
            itemMeta.setEnchantmentGlintOverride(overrideGlint ? true : null);
            return;
        }
        if (overrideGlint) {
            itemMeta.addEnchant(Enchantment.LURE, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            itemMeta.removeEnchant(Enchantment.LURE);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
    }

    public static @Nullable Entity getEntityByUuid(@NotNull World world, @NotNull UUID uuid) {
        if (HAS_ENTITY_BY_UUID) {
            return world.getEntity(uuid);
        } else {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(uuid)) {
                    return entity;
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void setAdvanceTime(@NotNull World world, boolean enabled) {
        if (HAS_GAME_RULE_REGISTRY_PAPER || HAS_GAME_RULE_REGISTRY_SPIGOT) {
            GameRule<Boolean> rule;
            if (HAS_GAME_RULE_REGISTRY_PAPER) {
                rule = GameRules.ADVANCE_TIME;
            } else {
                rule = (GameRule<Boolean>) Registry.GAME_RULE.getOrThrow(NamespacedKey.minecraft("advance_time"));
            }
            world.setGameRule(rule, enabled);
        } else {
            world.setGameRuleValue("doDaylightCycle", String.valueOf(enabled));
        }
    }
}
