package net.jadedmc.autopickup.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        player.giveExp(event.getExpToDrop());
        event.setExpToDrop(0);

        Collection<ItemStack> drops = event.getBlock().getDrops(player.getItemInUse());

        for(ItemStack item : drops) {
            player.getInventory().addItem(item);
        }

        event.setDropItems(false);
    }
}