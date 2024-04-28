package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.Unsafety;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class FastReflection extends NMSUtils {

    static Class<?> innerClass(Class<?> parentClass, Predicate<Class<?>> classPredicate) throws ClassNotFoundException {
        for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
            if (classPredicate.test(innerClass)) {
                return innerClass;
            }
        }
        throw new ClassNotFoundException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
    }

    static Optional<MethodHandle> optionalConstructor(Class<?> declaringClass, MethodHandles.Lookup lookup, MethodType type) throws IllegalAccessException {
        try {
            return Optional.of(lookup.findConstructor(declaringClass, type));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static PacketConstructor findPacketConstructor(Class<?> packetClass, MethodHandles.Lookup lookup) {
        try {
            MethodHandle constructor = lookup.findConstructor(packetClass, MethodType.methodType(void.class));
            return constructor::invoke;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return () -> Unsafety.allocateInstance(packetClass);
        }
    }

    @FunctionalInterface
    interface PacketConstructor {
        Object invoke() throws Throwable;
    }
}
