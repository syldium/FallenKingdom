package fr.devsylone.fallenkingdom.commands.brigadier;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

class CommandBuildContextSupplier {

    static final Class<?> COMMAND_BUILD_CONTEXT;
    private static final @Nullable Constructor<?> COMMAND_BUILD_CONTEXT_CONSTRUCTOR;
    private static final @Nullable Method CREATE_CONTEXT_METHOD;
    private static final @Nullable Method GET_WORLD_DATA_METHOD;
    private static final @Nullable Method GET_FEATURE_FLAGS_METHOD;
    private static final Method REGISTRY_ACCESS;

    static {
        try {
            COMMAND_BUILD_CONTEXT = NMSUtils.nmsClass("commands", "CommandBuildContext");
            final Constructor<?>[] constructors = COMMAND_BUILD_CONTEXT.getDeclaredConstructors();
            if (constructors.length == 0) { // >= 1.19.3
                COMMAND_BUILD_CONTEXT_CONSTRUCTOR = null;
                CREATE_CONTEXT_METHOD = Arrays.stream(COMMAND_BUILD_CONTEXT.getDeclaredMethods())
                        .filter(it -> it.getParameterCount() == 2 && COMMAND_BUILD_CONTEXT.isAssignableFrom(it
                                .getReturnType()) && Modifier.isStatic(it.getModifiers()))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchMethodException("Could not find CommandBuildContext.configurable"));

                final Class<?> worldDataCls = NMSUtils.nmsClass("world.level.storage", "SaveData");
                GET_WORLD_DATA_METHOD = Arrays.stream(PacketUtils.MINECRAFT_SERVER.getDeclaredMethods())
                        .filter(method -> method.getParameterCount() == 0 && !Modifier.isStatic(method.getModifiers())
                                && method.getReturnType().equals(worldDataCls))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchMethodException("Could not find MinecraftServer#getWorldData method"));
                final Class<?> featureFlagSetCls = NMSUtils.nmsClass("world.flag", "FeatureFlagSet");
                GET_FEATURE_FLAGS_METHOD = Arrays.stream(worldDataCls.getDeclaredMethods())
                        .filter(method -> method.getParameterCount() == 0
                                && method.getReturnType()
                                .equals(featureFlagSetCls) && !Modifier.isStatic(method.getModifiers()))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchMethodException("Could not find enabledFeatures method"));
            } else { // < 1.19.3
                COMMAND_BUILD_CONTEXT_CONSTRUCTOR = COMMAND_BUILD_CONTEXT.getDeclaredConstructors()[0];
                CREATE_CONTEXT_METHOD = null;
                GET_WORLD_DATA_METHOD = null;
                GET_FEATURE_FLAGS_METHOD = null;
            }

            final Class<?> registryAccess = COMMAND_BUILD_CONTEXT_CONSTRUCTOR != null
                    ? COMMAND_BUILD_CONTEXT_CONSTRUCTOR.getParameterTypes()[0]
                    : CREATE_CONTEXT_METHOD.getParameterTypes()[0];
            REGISTRY_ACCESS = Arrays.stream(PacketUtils.MINECRAFT_SERVER.getDeclaredMethods())
                    .filter(m -> registryAccess.isAssignableFrom(m.getReturnType()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("Cannot find MinecraftServer#registryAccess"));
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static @NotNull Object create() {
        try {
            final Object registryAccess = REGISTRY_ACCESS.invoke(PacketUtils.getNMSServer());
            if (COMMAND_BUILD_CONTEXT_CONSTRUCTOR != null) {
                return COMMAND_BUILD_CONTEXT_CONSTRUCTOR.newInstance(registryAccess);
            }
            final Object worldData = GET_WORLD_DATA_METHOD.invoke(PacketUtils.getNMSServer());
            final Object flags = GET_FEATURE_FLAGS_METHOD.invoke(worldData);
            return CREATE_CONTEXT_METHOD.invoke(null, registryAccess, flags);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
