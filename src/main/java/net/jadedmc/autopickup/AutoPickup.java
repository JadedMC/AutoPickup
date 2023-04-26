package net.jadedmc.autopickup;

import net.jadedmc.autopickup.commands.AutoPickupCMD;
import net.jadedmc.autopickup.listeners.BlockBreakListener;
import net.jadedmc.autopickup.listeners.EntityDeathListener;
import net.jadedmc.autopickup.listeners.PlayerFishListener;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is the main class of the plugin.
 * It links all parts together and registers them with the server.
 */
public final class AutoPickup extends JavaPlugin {
    private SettingsManager settingsManager;

    /**
     * Runs when the server is started.
     */
    @Override
    public void onEnable() {
        // Load config.yml
        settingsManager = new SettingsManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFishListener(this), this);

        // Register commands
        getCommand("autopickup").setExecutor(new AutoPickupCMD(this));

        // Pass plugin instance to InventoryUtils.
        new InventoryUtils(this);
    }

    /**
     * Gets the current settings manager instance.
     * @return Settings Manager.
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}