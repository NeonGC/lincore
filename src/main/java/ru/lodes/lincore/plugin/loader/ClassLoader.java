package ru.lodes.lincore.plugin.loader;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarFile;

public class ClassLoader extends URLClassLoader {

    public static ClassLoader[] loaders = new ClassLoader[500];
    public static int iteration = 0;

    public ClassLoader(URL[] urls) {
        super(urls);
    }

    public void created() {
        loaders[iteration] = this;
        iteration++;
    }

    public static void allUnload() {
        int i;
        for (i = 0;i < iteration;i++) {
            loaders[i].close();
        }
        iteration = 0;
        loaders = new ClassLoader[500];
    }

    @Override
    public void close() {
        try {
            Class clazz = URLClassLoader.class;
            Field ucp = clazz.getDeclaredField("ucp");
            ucp.setAccessible(true);
            Object sunMiscURLClassPath = ucp.get(this);
            Field loaders = sunMiscURLClassPath.getClass().getDeclaredField("loaders");
            loaders.setAccessible(true);
            Object collection = loaders.get(sunMiscURLClassPath);
            for (Object sunMiscURLClassPathJarLoader : ((Collection) collection).toArray()) {
                try {
                    Field loader = sunMiscURLClassPathJarLoader.getClass().getDeclaredField("jar");
                    loader.setAccessible(true);
                    Object jarFile = loader.get(sunMiscURLClassPathJarLoader);
                    ((JarFile) jarFile).close();
                } catch (Exception t) {
                }
            }
        } catch (Exception t) {
        }
    }
}
