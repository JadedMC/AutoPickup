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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This listens to the BlockBreakEvent event, which is called every time a player breaks a block.
 * We use this to automatically add block drops to the player's inventory.
 */
public class BlockBreakListener implements Listener {
    private final AutoPickupPlugin plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * @param plugin Instance of the plugin.
     */
    public BlockBreakListener(AutoPickupPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event BlockBreakEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Ignore players in creative mode.
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Exit if auto pickup for blocks is disabled.
        if(!plugin.getSettingsManager().getConfig().getBoolean("AutoPickup.Blocks")) {
            return;
        }

        // Exit if permissions are required and the player does not have them.
        if(plugin.getSettingsManager().getConfig().getBoolean("RequirePermission") && !player.hasPermission("autopickup.use")) {
            return;
        }

        // There is no way to modify drops, so to support custom drops, checks all dropped item entities 1 tick after the event.
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Collection<ItemStack> drops = new ArrayList<>();

            // Loop through all nearby entities to check for item entities.
            for(Entity entity : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 1, 1,1)) {

                // Skip any that aren't items.
                if(!(entity instanceof Item)) {
                    continue;
                }

                // Add the item to the dropped items collection.
                ItemStack item = ((Item) entity).getItemStack();
                drops.add(item);
                entity.remove();
            }

            // Add the dropped items to the player's inventory.
            Collection<ItemStack> remaining = InventoryUtils.addItems(player, drops);

            // Respawn any items that do not fit in the player's inventory.
            for(ItemStack drop : remaining) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            }
        }, 1);

        // Process stacked crops, like bamboo, cactus, and sugar cane.
        Collection<Material> crops = Arrays.asList(Material.BAMBOO, Material.CACTUS, Material.SUGAR_CANE);
        Block above = event.getBlock().getRelative(BlockFace.UP);
        if(crops.contains(event.getBlock().getType()) && crops.contains(above.getType())) {
            player.breakBlock(above);
        }
    }
}