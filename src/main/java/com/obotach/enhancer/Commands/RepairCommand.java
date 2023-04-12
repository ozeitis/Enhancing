package com.obotach.enhancer.Commands;

import com.obotach.enhancer.CustomItems;
import com.obotach.enhancer.Enhancing;
import com.obotach.enhancer.CustomItems.CustomItemKeys;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RepairCommand implements CommandExecutor {

    private Enhancing plugin;

    public RepairCommand(Enhancing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (isEnhancedItem(heldItem)) {
            int memoryFragmentsUsed = repairItemWithMemoryFragments(player, heldItem);

            if (memoryFragmentsUsed > 0) {
                player.sendMessage(ChatColor.GREEN + "Successfully repaired your item using " + memoryFragmentsUsed + " memory fragments.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Your item is already at max durability.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must hold an enhanced item to repair it.");
        }

        return true;
    }

    private int repairItemWithMemoryFragments(Player player, ItemStack itemToRepair) {
        PlayerInventory inventory = player.getInventory();
        int memoryFragmentsUsed = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);

            if (isMemoryFragment(currentItem)) {
                int repairAmount = currentItem.getAmount();
                int remainingDurability = itemToRepair.getType().getMaxDurability() - itemToRepair.getDurability();

                if (repairAmount >= remainingDurability) {
                    repairAmount = remainingDurability;
                    currentItem.setAmount(currentItem.getAmount() - repairAmount);
                    itemToRepair.setDurability((short) 0);
                    memoryFragmentsUsed += repairAmount;
                    break;
                } else {
                    currentItem.setAmount(0);
                    itemToRepair.setDurability((short) (itemToRepair.getDurability() - repairAmount));
                    memoryFragmentsUsed += repairAmount;
                    remainingDurability -= repairAmount;
                }

                if (currentItem.getAmount() <= 0) {
                    inventory.clear(i);
                } else {
                    inventory.setItem(i, currentItem);
                }
            }
        }

        return memoryFragmentsUsed;
    }

    private boolean isEnhancedItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
    
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
    
        NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
        return itemMeta.getPersistentDataContainer().has(enhancementLevelKey, PersistentDataType.INTEGER);
    }    

    public static boolean isMemoryFragment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(CustomItemKeys.MEMORY_FRAGMENT_KEY, PersistentDataType.INTEGER);
    }
}
