package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class InternalRegistry<T> {

    private static final Class<?> REGISTRY;
    private static final Method REGISTRY_GET_RESOURCE_KEY;
    private static final Method REGISTRY_VALUE_BY_KEY;

    static {
        try {
            REGISTRY = NMSUtils.nmsClass("core", "IRegistry");
            REGISTRY_GET_RESOURCE_KEY = Arrays.stream(REGISTRY.getMethods())
                    .filter(m -> m.getParameterCount() == 0 && m.getReturnType().equals(MinecraftKey.RESOURCE))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            REGISTRY_VALUE_BY_KEY = Arrays.stream(REGISTRY.getMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(MinecraftKey.IDENTIFIER) && m.getReturnType().equals(Object.class))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object handle;

    public InternalRegistry(@NotNull NamespacedKey key) throws InvocationTargetException, IllegalAccessException {
        this.handle = registryByResourceKey(key);
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get(@NotNull NamespacedKey identifier) throws InvocationTargetException, IllegalAccessException {
        return (T) REGISTRY_VALUE_BY_KEY.invoke(this.handle, MinecraftKey.identifier(identifier));
    }

    private static @NotNull Object registryByResourceKey(@NotNull NamespacedKey key) throws IllegalAccessException, InvocationTargetException {
        Object registryKey = MinecraftKey.registry(key);
        for (Field field : REGISTRY.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !REGISTRY.isAssignableFrom(field.getType())) {
                continue;
            }
            if (REGISTRY_GET_RESOURCE_KEY.invoke(field.get(null)).equals(registryKey)) {
                return field.get(null);
            }
        }
        throw new IllegalArgumentException("no registry for " + registryKey);
    }
}
