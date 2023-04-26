package net.jadedmc.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class InventoryUtils {

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
