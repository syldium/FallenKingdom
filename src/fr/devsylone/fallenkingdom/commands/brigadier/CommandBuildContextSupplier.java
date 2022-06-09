package fr.devsylone.fallenkingdom.commands.brigadier;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

class CommandBuildContextSupplier {

    static final Class<?> COMMAND_BUILD_CONTEXT;
    private static final Constructor<?> COMMAND_BUILD_CONTEXT_CONSTRUCTOR;
    private static final Method REGISTRY_ACCESS;

    static {
        try {
            COMMAND_BUILD_CONTEXT = NMSUtils.nmsClass("commands", "CommandBuildContext");
            COMMAND_BUILD_CONTEXT_CONSTRUCTOR = COMMAND_BUILD_CONTEXT.getDeclaredConstructors()[0];

            final Class<?> registryAccess = COMMAND_BUILD_CONTEXT_CONSTRUCTOR.getParameterTypes()[0];
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
            return COMMAND_BUILD_CONTEXT_CONSTRUCTOR.newInstance(REGISTRY_ACCESS.invoke(PacketUtils.getNMSServer()));
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
