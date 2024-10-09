/*
 * This file is part of AutoPickup, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.autopickup;

import net.jadedmc.autopickup.commands.AutoPickupCMD;
import net.jadedmc.autopickup.listeners.BlockBreakListener;
import net.jadedmc.autopickup.listeners.EntityDeathListener;
import net.jadedmc.autopickup.listeners.PlayerFishListener;
import net.jadedmc.autopickup.listeners.ReloadListener;
import net.jadedmc.autopickup.utils.ChatUtils;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is the main class of the plugin.
 * It links all parts together and registers them with the server.
 */
public final class AutoPickupPlugin extends JavaPlugin {
    private HookManager hookManager;
    private SettingsManager settingsManager;

    /**
     * Runs when the server is started.
     */
    @Override
    public void onEnable() {
        // Setup utilities.
        ChatUtils.initialize(this);
        InventoryUtils.initialize(this);

        // Load config.yml
        settingsManager = new SettingsManager(this);
        hookManager = new HookManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFishListener(this), this);

        // Supports BetterReload if installed.
        if(this.hookManager.useBetterReload()) getServer().getPluginManager().registerEvents(new ReloadListener(this), this);

        // Register commands
        getCommand("autopickup").setExecutor(new AutoPickupCMD(this));

        // Enables bStats statistics tracking.
        new Metrics(this, 18302);
    }

    /**
     * Runs when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        // Disables adventure to prevent memory leaks.
        ChatUtils.disable();
    }

    /**
     * Get the Hook Manager, which returns an object that keeps track of hooks into other plugins.
     * @return HookManager.
     */
    public HookManager getHookManager() {
        return this.hookManager;
    }

    /**
     * Gets the current settings manager instance.
     * @return Settings Manager.
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    /**
     * Reloads the plugin configuration and updates important values.
     */
    public void reload() {
        this.settingsManager.reloadConfig();
    }
}