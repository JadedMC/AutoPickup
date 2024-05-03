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
package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.AutoPickupPlugin;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This listens to the EntityDeathEvent event, which is called every time an entity dies.
 * We use this to automatically add mob drops to the player's inventory.
 */
public class EntityDeathListener implements Listener {
    private final AutoPickupPlugin plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDeathListener(AutoPickupPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event EntityDeathEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        // Makes sure the killer is still online.
        if(killer == null) {
            return;
        }

        // Makes sure the entity is not a player.
        if(event.getEntity() instanceof Player) {
            return;
        }

        // Exit if auto pickup for mobs is disabled.
        if(!plugin.getSettingsManager().getConfig().getBoolean("AutoPickup.Mobs")) {
            return;
        }

        // Exit if permissions are required and the player does not have them.
        if(plugin.getSettingsManager().getConfig().getBoolean("RequirePermission") && !killer.hasPermission("autopickup.use")) {
            return;
        }

        // Add dropped xp to the player.
        killer.giveExp(event.getDroppedExp());
        event.setDroppedExp(0);

        // Clear the list of dropped items.
        Collection<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        // Adds the item caught to the player's inventory.
        Collection<ItemStack> remaining = InventoryUtils.addItems(killer, drops);

        // Drops all items that could not fit in the player's inventory.
        event.getDrops().addAll(remaining);
    }
}