package io.github.wamel04.mine.regener;

import io.github.wamel04.mine.BukkitInitializer;
import io.github.wamel04.mine.util.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RegenerRegister {

    private static BukkitInitializer plugin = BukkitInitializer.getInstance();

    public static void start() {
        Set<Class<?>> classes = getClasses("io.github.wamel04.mine.regener.list");

        int i = 0;
        for (Class clazz : classes) {
            register(clazz);
            i++;
        }

        Bukkit.getConsoleSender().sendMessage("§9[Mine] 성공적으로 " + i + "개의 Regener를 로드했습니다.");
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            JavaPlugin pluginObject = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(plugin.getName());
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(pluginObject);
            JarFile jarFile = new JarFile(file);

            for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements();) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");

                if (name.startsWith(packageName)) {
                    if (name.endsWith(".class") && !name.contains("$")) { // 내부 클래스 포함 X
                        classes.add(Class.forName(name.substring(0, name.length() - 6)));
                        continue;
                    } if (!name.endsWith(".class")) { // 재귀적으로 해당 패키지에 위치한 모든 클래스를 불러 옴
                        classes.addAll(getClasses("name"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void register(Class<? extends Regener> clazz) {
        try {
            Constructor<? extends Regener> constructor = clazz.getDeclaredConstructor(
                    Material.class, int.class, SimpleItem.class, int.class, int.class, boolean.class, Integer.class, SimpleItem.class
            );
            Object instance = constructor.newInstance(null, 0, null, 0, 0, true, null, null);

            plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
