package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class MinecraftKey {

    public static final Class<?> IDENTIFIER;
    public static final Class<?> RESOURCE;

    private static final Constructor<?> MINECRAFT_KEY_CONSTRUCTOR;
    private static final Method RESOURCE_KEY_FACTORY;

    static {
        try {
            IDENTIFIER = NMSUtils.nmsClass("resources", "MinecraftKey");
            MINECRAFT_KEY_CONSTRUCTOR = IDENTIFIER.getConstructor(String.class, String.class);
            MINECRAFT_KEY_CONSTRUCTOR.setAccessible(true);

            RESOURCE = NMSUtils.nmsClass("resources", "ResourceKey");
            RESOURCE_KEY_FACTORY = Arrays.stream(RESOURCE.getMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(IDENTIFIER))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private MinecraftKey() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull Object identifier(@NotNull NamespacedKey key) {
        try {
            return MINECRAFT_KEY_CONSTRUCTOR.newInstance(key.getNamespace(), key.getKey());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Object registry(@NotNull NamespacedKey key) {
        try {
            return RESOURCE_KEY_FACTORY.invoke(null, identifier(key));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
