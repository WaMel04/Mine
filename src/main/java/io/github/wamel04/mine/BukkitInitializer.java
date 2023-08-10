package io.github.wamel04.mine;

import io.github.wamel04.mine.regener.RegenerRegister;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitInitializer extends JavaPlugin {

    private static BukkitInitializer instance;

    @Override
    public void onEnable() {
        instance = this;

        RegenerRegister.start();
    }

    @Override
    public void onDisable() {
    }

    public static BukkitInitializer getInstance() {
        return instance;
    }
}
