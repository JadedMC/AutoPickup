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
package net.jadedmc.autopickup.utils;

import net.jadedmc.autopickup.AutoPickupPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Useful Inventory utilities.
 */
public class InventoryUtils {
    private static AutoPickupPlugin plugin;

    /**
     * Passes a plugin instance to the utility.
     * @param pl Instance of the plugin.
     */
    public InventoryUtils(AutoPickupPlugin pl) {
        plugin = pl;
    }

    /**
     * Add items to a player's inventory.
     * @param player Player to add items to.
     * @param items Collection of items to add.
     * @return Collection of items that did not fit.
     */
    public static Collection<ItemStack> addItems(Player player, Collection<ItemStack> items) {
        Collection<ItemStack> remaining = new ArrayList<>();
        boolean full = false;

        for(ItemStack item : items) {
            if(hasFullInventory(player)) {
                full = true;
                remaining.add(item);
                continue;
            }

            player.getInventory().addItem(item);
        }

        if(full) {

            // Display the inventory full message.
            if(plugin.getSettingsManager().getConfig().isSet("InventoryFull.Message")) {
                ChatUtils.chat(player, plugin.getSettingsManager().getConfig().getString("InventoryFull.Message"));
            }

            // Play the inventory full sound.
            if(plugin.getSettingsManager().getConfig().isSet("InventoryFull.Sound")) {
                player.playSound(player.getLocation(), Sound.valueOf(plugin.getSettingsManager().getConfig().getString("InventoryFull.Sound")), 1, 1);
            }
        }

        return remaining;
    }

    /**
     * Check if a player's inventory is full.
     * @param player Player to check inventory of.
     * @return Whether the inventory is full.
     */
    public static boolean hasFullInventory(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}
