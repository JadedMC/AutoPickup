package net.jadedmc.autopickup.utils;

import net.jadedmc.autopickup.AutoPickup;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Useful Inventory utilities.
 */
public class InventoryUtils {
    private static AutoPickup plugin;

    /**
     * Passes a plugin instance to the utility.
     * @param pl Instance of the plugin.
     */
    public InventoryUtils(AutoPickup pl) {
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
