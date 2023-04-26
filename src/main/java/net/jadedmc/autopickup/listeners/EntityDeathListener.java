package net.jadedmc.autopickup.listeners;

import net.jadedmc.autopickup.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class EntityDeathListener implements Listener {

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

        // Add dropped xp to the player.
        killer.giveExp(event.getDroppedExp());
        event.setDroppedExp(0);

        Collection<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();


        Collection<ItemStack> remaining = InventoryUtils.addItems(killer, drops);
        event.getDrops().addAll(remaining);
    }
}