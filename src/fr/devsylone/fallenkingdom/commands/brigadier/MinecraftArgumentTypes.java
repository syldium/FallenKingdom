/*
 * Original file : https://github.com/lucko/commodore/blob/master/src/main/java/me/lucko/commodore/MinecraftArgumentTypes.java
 *
 * This file is part of commodore, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package fr.devsylone.fallenkingdom.commands.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.version.Version.VersionType;
import fr.devsylone.fallenkingdom.version.tracker.MinecraftKey;
import fr.devsylone.fallenkingdom.version.tracker.InternalRegistry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

/**
 * A registry of the {@link ArgumentType}s provided by Minecraft.
 */
public final class MinecraftArgumentTypes {
    private MinecraftArgumentTypes() {}

    private static final ArgumentTypeRegistry ARGUMENT_TYPE_REGISTRY;

    // ArgumentRegistry#BY_CLASS (obfuscated) field
    private static final Field BY_CLASS_MAP_FIELD;

    static {
        try {
            Class<?> argumentRegistry;
            if (VersionType.V1_19.isHigherOrEqual()) {
                argumentRegistry = NMSUtils.nmsClass("commands.synchronization", "ArgumentTypeInfos");
                ARGUMENT_TYPE_REGISTRY = new ArgumentTypeRegistry1_19();
            } else {
                argumentRegistry = NMSUtils.nmsClass("commands.synchronization", "ArgumentRegistry");
                ARGUMENT_TYPE_REGISTRY = new ArgumentTypeRegistry1_13(argumentRegistry);
            }

            BY_CLASS_MAP_FIELD = Arrays.stream(argumentRegistry.getDeclaredFields())
                    .filter(field -> field.getType().equals(Map.class))
                    .filter(field -> {
                        final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        final Type param = parameterizedType.getActualTypeArguments()[0];
                        if (!(param instanceof ParameterizedType)) {
                            return false;
                        }
                        return ((ParameterizedType) param).getRawType().equals(Class.class);
                    })
                    .findFirst().orElseThrow(NoSuchFieldException::new);
            BY_CLASS_MAP_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Gets a registered argument type class by key.
     *
     * @param key the key
     * @return the returned argument type class
     * @throws IllegalArgumentException if no such argument is registered
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey key) throws IllegalArgumentException {
        try {
            Object entry = ARGUMENT_TYPE_REGISTRY.find(key);
            if (entry == null) {
                throw new IllegalArgumentException(key.toString());
            }

            final Map<Class<?>, Object> map = (Map<Class<?>, Object>) BY_CLASS_MAP_FIELD.get(null);
            for (final Map.Entry<Class<?>, Object> mapEntry : map.entrySet()) {
                if (mapEntry.getValue() == entry) {
                    return (Class<? extends ArgumentType<?>>) mapEntry.getKey();
                }
            }
            throw new IllegalArgumentException(key.toString());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a registered argument type by key.
     *
     * @param key the key
     * @return the returned argument
     * @throws IllegalArgumentException if no such argument is registered
     */
    public static ArgumentType<?> getByKey(NamespacedKey key) throws IllegalArgumentException {
        try {
            final Constructor<? extends ArgumentType<?>> constructor = getClassByKey(key).getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Construct a minecraft argument type with params
     * @param key the key
     * @param argTypes
     * @param args
     * @return
     */
    public static ArgumentType<?> constructMinecraftArgumentType(NamespacedKey key, Class<?>[] argTypes, Object... args) {
        try {
            final Constructor<? extends ArgumentType<?>> constructor = MinecraftArgumentTypes.getClassByKey(key).getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private interface ArgumentTypeRegistry {
        @Nullable Object find(@NotNull NamespacedKey identifier) throws ReflectiveOperationException;
    }

    private static class ArgumentTypeRegistry1_13 implements ArgumentTypeRegistry {

        private final Method argumentRegistryGetByKey;

        private ArgumentTypeRegistry1_13(@NotNull Class<?> argumentRegistry) throws ReflectiveOperationException {
            argumentRegistryGetByKey = Arrays.stream(argumentRegistry.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 1)
                    .filter(method -> MinecraftKey.IDENTIFIER.equals(method.getParameterTypes()[0]))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            argumentRegistryGetByKey.setAccessible(true);
        }

        @Override
        public @Nullable Object find(@NotNull NamespacedKey identifier) throws ReflectiveOperationException {
            return this.argumentRegistryGetByKey.invoke(null, MinecraftKey.identifier(identifier));
        }
    }

    private static class ArgumentTypeRegistry1_19 implements ArgumentTypeRegistry {

        private final InternalRegistry<Object> registry = new InternalRegistry<>(NamespacedKey.minecraft("command_argument_type"));

        private ArgumentTypeRegistry1_19() throws InvocationTargetException, IllegalAccessException {
        }

        @Override
        public @Nullable Object find(@NotNull NamespacedKey identifier) throws ReflectiveOperationException {
            return this.registry.get(identifier);
        }
    }
}