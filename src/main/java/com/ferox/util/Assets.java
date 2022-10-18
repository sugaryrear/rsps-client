package com.ferox.util;

import java.net.URL;

/**
 * @author Patrick van Elderen | November, 11, 2020, 16:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public final class Assets {

    public static URL getResource(String... dirs) {
        return getResource(String.join("/", dirs), getClassLoaderForClass(Assets.class));
    }

    public static URL getResource(String resourceName) {
        return getResource(resourceName, getClassLoaderForClass(Assets.class));
    }

    public static URL getResource(String resourceName, ClassLoader classLoader) {
        return classLoader.getResource(resourceName);
    }

    private static ClassLoader getClassLoaderForClass(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null)
            return ClassLoader.getSystemClassLoader();
        return classLoader;
    }

}
