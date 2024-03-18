package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.AutoPickup;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This listens to the PlayerFishEvent event, which is called every time a player catches a fish.
 * We use this to automatically add caught items to the player's inventory.
 */
public class PlayerFishListener implements Listener {
    private final AutoPickup plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerFishListener(AutoPickup plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerFishEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        // Exit if auto pickup for fishing is disabled.
        if(!plugin.getSettingsManager().getConfig().getBoolean("AutoPickup.Fishing")) {
            return;
        }

        // Exit if permissions are required and the player does not have them.
        if(plugin.getSettingsManager().getConfig().getBoolean("RequirePermission") && !player.hasPermission("autopickup.use")) {
            return;
        }

        // Makes sure the caught entity is an item.
        if(!(event.getCaught() instanceof Item)) {
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Collection<ItemStack> drops = new ArrayList<>();

            // Loop through all nearby entities to check for item entities.
            for(Entity entity : event.getCaught().getWorld().getNearbyEntities(event.getCaught().getLocation(), 1, 1,1)) {

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
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }, 1);
    }
}