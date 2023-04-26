package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.AutoPickup;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

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
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        // Give the player the dropped experience.
        player.giveExp(event.getExpToDrop());
        event.setExpToDrop(0);

        // Makes sure the caught entity is an item.
        if(!(event.getCaught() instanceof Item)) {
            return;
        }

        // Exit if auto pickup for fishing is disabled.
        if(!plugin.getSettingsManager().getConfig().getBoolean("AutoPickup.Fishing")) {
            return;
        }

        // Get the ItemStack caught.
        ItemStack item = ((Item) event.getCaught()).getItemStack();

        // Delete the item entity that was caught.
        event.getCaught().remove();

        // Adds the item caught to the player's inventory.
        Collection<ItemStack> remaining = InventoryUtils.addItems(player, Collections.singletonList(item));

        // Respawn any items that do not fit in the player's inventory.
        for(ItemStack itemStack : remaining) {
            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
    }
}
