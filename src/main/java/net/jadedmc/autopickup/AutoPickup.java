package net.jadedmc.autopickup;

import net.jadedmc.autopickup.listeners.BlockBreakListener;
import net.jadedmc.autopickup.listeners.EntityDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPickup extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
    }
}