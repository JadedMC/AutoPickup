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
import net.jadedmc.autopickup.utils.FoodUtils;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public EntityDeathListener(@NotNull final AutoPickupPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event EntityDeathEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        final Player killer = event.getEntity().getKiller();

        // Makes sure the killer is still online.
        if(killer == null) {
            return;
        }

        // Makes sure the entity is not a player.
        if(event.getEntity() instanceof Player) {
            return;
        }

        // Exit if auto pickup for mobs is disabled.
        if(!plugin.getConfigManager().getConfig().getBoolean("AutoPickup.Mobs")) {
            return;
        }

        // Exit if permissions are required and the player does not have them.
        if(plugin.getConfigManager().getConfig().getBoolean("RequirePermission") && !killer.hasPermission("autopickup.use")) {
            return;
        }

        // Clear the list of dropped items.
        final Collection<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        // if auto-cook is enabled, all items are cooked and given to the player.
        if (plugin.getConfigManager().getConfig().getBoolean("Settings.auto-cook")){
            if (plugin.getConfigManager().getConfig().getBoolean("RequirePermission") && killer.hasPermission("autopickup.use.autocook")){
                List<ItemStack> drop = new ArrayList<>(drops);
                drop.replaceAll(itemStack -> {
                    Material cookedMaterial = FoodUtils.getCookedVersion(itemStack.getType());
                    if (cookedMaterial != null) {
                        return new ItemStack(cookedMaterial, itemStack.getAmount());
                    }
                    return itemStack;
                });
                drops.clear();
                drops.addAll(drop);
            }
        }

        // Adds the item caught to the player's inventory.
        final Collection<ItemStack> remaining = InventoryUtils.addItems(killer, drops);

        // Drops all items that could not fit in the player's inventory.
        event.getDrops().addAll(remaining);
    }
}