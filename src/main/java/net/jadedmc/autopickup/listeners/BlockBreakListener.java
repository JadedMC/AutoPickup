package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.AutoPickup;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This listens to the BlockBreakEvent event, which is called every time a player breaks a block.
 * We use this to automatically add block drops to the player's inventory.
 */
public class BlockBreakListener implements Listener {
    private final AutoPickup plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * @param plugin Instance of the plugin.
     */
    public BlockBreakListener(AutoPickup plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event BlockBreakEvent.
     */
    @EventHandler
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

        // Give the player the dropped experience.
        player.giveExp(event.getExpToDrop());
        event.setExpToDrop(0);

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
    }
}