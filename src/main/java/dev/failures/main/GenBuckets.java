package dev.failures.main;

import dev.failures.main.Commands.ShopCommand;
import dev.failures.main.Listeners.BucketListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class GenBuckets extends JavaPlugin {
    public static GenBuckets instance;
    public static Economy econ = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();

        getServer().getPluginManager().registerEvents(new BucketListener(this), this);
        getCommand("gen").setExecutor(new ShopCommand(this));

        instance = this;

    }

    public static GenBuckets getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
