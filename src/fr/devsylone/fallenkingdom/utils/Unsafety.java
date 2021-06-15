package fr.devsylone.fallenkingdom.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class Unsafety {

    private static Unsafe UNSAFE;

    private Unsafety() {}

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object allocateInstance(Class<?> clazz) throws InstantiationException {
        return UNSAFE.allocateInstance(clazz);
    }
}
