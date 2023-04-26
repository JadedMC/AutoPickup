package net.jadedmc.autopickup;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Manages the configurable settings in the plugin.
 */
public class SettingsManager {
    private FileConfiguration config;
    private final File configFile;

    /**
     * Loads or Creates configuration files.
     * @param plugin Instance of the plugin.
     */
    public SettingsManager(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Get the config.yml FileConfiguration.
     * @return config.yml FileConfiguration.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Update the configuration file.
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Save the configuration file.
     */
    public void save() {
        try {
            config.save(configFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}