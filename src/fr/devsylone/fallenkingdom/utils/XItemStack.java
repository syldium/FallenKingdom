package fr.devsylone.fallenkingdom.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.devsylone.fallenkingdom.version.tracker.ChatMessage.CHAT_BASE_COMPONENT;

public class XItemStack {

    private final static Method CHAT_COMPONENT_FROM_JSON;
    private final static Method CHAT_COMPONENT_TO_JSON;
    private static final Object EMPTY_REGISTRY;

    public final static Class<?> ITEM_STACK;
    private final static Method AS_NMS_COPY;
    private final static Method AS_CRAFT_MIRROR;

    private final static Field DISPLAY_NAME;
    private final static Field LORE;

    private final static boolean HAS_COMPONENT_API;
    private final static boolean HAS_PLAYER_PROFILE_API;
    private final static boolean SERIALIZED_VIEW; // Spigot en 2021

    static {
        try {
            Class<?> chatSerializer = NMSUtils.nmsClass("network.chat", "IChatBaseComponent$ChatSerializer");

            Optional<Class<?>> registryAccess = NMSUtils.nmsOptionalClass("core", "IRegistryCustom");
            Optional<Method> parseFromJson = Arrays.stream(chatSerializer.getDeclaredMethods())
                    .filter(m -> CHAT_BASE_COMPONENT.isAssignableFrom(m.getReturnType()))
                    .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{String.class}))
                    .findFirst();
            if (parseFromJson.isPresent()) {
                CHAT_COMPONENT_FROM_JSON = parseFromJson.get();
                CHAT_COMPONENT_TO_JSON = Arrays.stream(chatSerializer.getDeclaredMethods())
                        .filter(m -> m.getReturnType().equals(String.class))
                        .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{CHAT_BASE_COMPONENT}))
                        .findAny().orElseThrow(RuntimeException::new);
                EMPTY_REGISTRY = null;
            } else {
                CHAT_COMPONENT_FROM_JSON = Arrays.stream(chatSerializer.getDeclaredMethods())
                        .filter(m -> CHAT_BASE_COMPONENT.isAssignableFrom(m.getReturnType()))
                        .filter(m -> {
                            Class<?>[] params = m.getParameterTypes();
                            return params.length == 2 && params[0].equals(String.class) && params[1].isAssignableFrom(registryAccess.get());
                        })
                        .findAny().orElseThrow(RuntimeException::new);
                CHAT_COMPONENT_TO_JSON = Arrays.stream(chatSerializer.getDeclaredMethods())
                        .filter(m -> m.getReturnType().equals(String.class))
                        .filter(m -> {
                            Class<?>[] params = m.getParameterTypes();
                            return params.length == 2 && params[0].equals(CHAT_BASE_COMPONENT) && params[1].isAssignableFrom(registryAccess.get());
                        })
                        .findAny().orElseThrow(RuntimeException::new);
                EMPTY_REGISTRY = Arrays.stream(registryAccess.get().getFields())
                        .filter(field -> Modifier.isStatic(field.getModifiers()) && registryAccess.get().isAssignableFrom(field.getType()))
                        .findAny().orElseThrow(RuntimeException::new)
                        .get(null);
            }

            ITEM_STACK = NMSUtils.nmsClass("world.item", "ItemStack");
            AS_NMS_COPY = NMSUtils.obcClass("inventory.CraftItemStack")
                    .getDeclaredMethod("asNMSCopy", ItemStack.class);
            AS_CRAFT_MIRROR = NMSUtils.obcClass("inventory.CraftItemStack")
                    .getDeclaredMethod("asCraftMirror", ITEM_STACK);

            boolean hasComponentApi;
            try {
                ItemMeta.class.getMethod("setDisplayNameComponent", BaseComponent[].class);
                hasComponentApi = true;
            } catch (NoSuchMethodException e) {
                hasComponentApi = false;
            }
            HAS_COMPONENT_API = hasComponentApi;
            boolean hasPlayerProfileApi;
            try {
                Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
                Player.class.getMethod("setPlayerProfile", PlayerProfile.class);
                hasPlayerProfileApi = true;
            } catch (ReflectiveOperationException e) {
                hasPlayerProfileApi = false;
            }
            HAS_PLAYER_PROFILE_API = hasPlayerProfileApi;

            Class<?> craftMeta = NMSUtils.obcClass("inventory.CraftMetaItem");
            DISPLAY_NAME = craftMeta.getDeclaredField("displayName");
            DISPLAY_NAME.setAccessible(true);
            LORE = craftMeta.getDeclaredField("lore");
            LORE.setAccessible(true);
            SERIALIZED_VIEW = String.class.equals(DISPLAY_NAME.getType());
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static @NotNull ItemStack setDisplayNameAndLore(@NotNull ItemStack itemStack, @NotNull BaseComponent[] displayName, @NotNull List<@NotNull BaseComponent[]> lore) throws InvocationTargetException, IllegalAccessException {
        final ItemMeta meta = itemStack.getItemMeta();
        if (HAS_COMPONENT_API) {
            meta.setDisplayNameComponent(displayName);
            meta.setLoreComponents(lore);
        } else {
            final String jsonDisplayName = ComponentSerializer.toString(displayName);
            final Stream<String> jsonLore = lore.stream().map(ComponentSerializer::toString);
            DISPLAY_NAME.set(meta, SERIALIZED_VIEW ? jsonDisplayName : CHAT_COMPONENT_FROM_JSON.invoke(null, jsonDisplayName));
            LORE.set(meta, SERIALIZED_VIEW ? jsonLore.collect(Collectors.toList()) : jsonLore.map(json -> {
                try {
                    return CHAT_COMPONENT_FROM_JSON.invoke(null, json);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList()));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static Object asCraftItem(ItemStack itemStack) {
        try {
            return AS_NMS_COPY.invoke(null, itemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack asBukkitItem(Object nmsItemStack) {
        if (nmsItemStack == null) {
            return null;
        }
        if (!ITEM_STACK.isAssignableFrom(nmsItemStack.getClass())) {
            throw new IllegalArgumentException("Can't convert " + nmsItemStack + " to bukkit item stack.");
        }

        try {
            return (ItemStack) AS_CRAFT_MIRROR.invoke(null, nmsItemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static BaseComponent[] getTextComponent(Object obj, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.getType().equals(CHAT_BASE_COMPONENT) && i++ == count) {
                field.setAccessible(true);
                String json;
                if (EMPTY_REGISTRY != null) {
                    json = (String) CHAT_COMPONENT_TO_JSON.invoke(null, field.get(obj), EMPTY_REGISTRY);
                } else {
                    json = (String) CHAT_COMPONENT_TO_JSON.invoke(null, field.get(obj));
                }
                return ComponentSerializer.parse(json);
            }
        }
        throw new RuntimeException("Text component field not found");
    }

    @Contract("_, _ -> param1")
    public static @NotNull SkullMeta applyBase64Texture(@NotNull SkullMeta meta, @NotNull String texture) {
        if (HAS_PLAYER_PROFILE_API) {
            final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
            profile.setProperty(new ProfileProperty("textures", texture));
            meta.setPlayerProfile(profile);
        } else {
            SkullUtils.applySkin(meta, texture);
        }
        return meta;
    }
}
