package net.jadedmc.autopickup;

import net.jadedmc.autopickup.listeners.BlockBreakListener;
import net.jadedmc.autopickup.listeners.EntityDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is the main class of the plugin.
 * It links all parts together and registers them with the server.
 */
public final class AutoPickup extends JavaPlugin {

    /**
     * Runs when the server is started.
     */
    @Override
    public void onEnable() {
        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
    }
}