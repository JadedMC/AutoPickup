package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.AutoPickup;
import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    private final AutoPickup plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDeathListener(AutoPickup plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event EntityDeathEvent.
     */
    @EventHandler
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