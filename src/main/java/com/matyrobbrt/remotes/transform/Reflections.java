package com.matyrobbrt.remotes.transform;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public final class Reflections {
    private static final Unsafe UNSAFE;

    public static final MethodHandles.Lookup HANDLE;

    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);

            HANDLE = getStaticField(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"));
        } catch (Exception exception) {
            throw new RuntimeException("hmmmmm");
        }
    }

    public static <T> T getStaticField(Field field) {
        return (T) UNSAFE.getObject(UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field));
    }
}
