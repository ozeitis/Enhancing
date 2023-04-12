package com.obotach.enhancer.Listeners;

import com.obotach.enhancer.CustomItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockBreakListener implements Listener {

    private final Random random = new Random();
    private double memoryFragmentDropChance;

    public BlockBreakListener(double memoryFragmentDropChance) {
        this.memoryFragmentDropChance = memoryFragmentDropChance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if the block is solid and not an air block
        if (block.getType().isSolid() && block.getType() != Material.AIR) {
            // 1% chance
            if (random.nextDouble() < memoryFragmentDropChance) {
                ItemStack memoryFragment = CustomItems.createMemoryFragment();
                block.getWorld().dropItemNaturally(block.getLocation(), memoryFragment);
            }
        }
    }
}
