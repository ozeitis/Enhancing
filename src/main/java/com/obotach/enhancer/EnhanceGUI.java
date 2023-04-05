package com.obotach.enhancer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

public class EnhanceGUI {

    public static final String INVENTORY_TITLE = ChatColor.BLUE + "Enhance Item";
    private final Enhancing plugin;

    public EnhanceGUI(Enhancing plugin) {
        this.plugin = plugin;
    }

    public static Inventory createEnhanceInventory() {
        Component titleComponent = Component.text(INVENTORY_TITLE);
        Inventory enhanceInventory = Bukkit.createInventory(null, 45, titleComponent); // Increase the inventory size to 36
    
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        if (placeholderMeta != null) {
            placeholderMeta.setDisplayName(ChatColor.GRAY + " ");
            placeholder.setItemMeta(placeholderMeta);
        }
    
        for (int i = 0; i < 45; i++) {
            if (i != 19 && i != 22 && i != 25 && i != 16 && i != 34) { // Add protection rune slot at index 34
                enhanceInventory.setItem(i, placeholder);
            }
        }
        // set block at index 16 to be the show percent success block
        enhanceInventory.setItem(16, createSuccessChancePanel(0));
    
        ItemStack enhanceButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta enhanceButtonMeta = enhanceButton.getItemMeta();
        if (enhanceButtonMeta != null) {
            enhanceButtonMeta.setDisplayName(ChatColor.GREEN + "Enhance");
            enhanceButton.setItemMeta(enhanceButtonMeta);
        }
    
        enhanceInventory.setItem(25, createEnhanceButton());
    
        return enhanceInventory;
    }
    
    
    

    public static void openEnhanceGUI(Player player) {
        player.openInventory(createEnhanceInventory());
    }
    
    public static ItemStack createEnhanceButton() {
        ItemStack enhanceButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = enhanceButton.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Enhance");
            enhanceButton.setItemMeta(meta);
        }
    
        return enhanceButton;
    }

    public static ItemStack createSuccessChancePanel(double successChance) {
        ItemStack enhanceButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = enhanceButton.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Success Chance");
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(ChatColor.AQUA + "" + ChatColor.AQUA + String.format("%.2f", successChance) + "%"));
            meta.lore(lore);
            enhanceButton.setItemMeta(meta);
        }
    
        return enhanceButton;
    }

    public void showSuccessBlock(Player player, Inventory inventory) {
        ItemStack successBlock = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = successBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.GREEN + "Enhancement Success!"));
            successBlock.setItemMeta(meta);
        }
        inventory.setItem(25, successBlock);
        // update the success chance panel
        updateSuccessChancePanel(inventory);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateSuccessChancePanel(inventory);
        }, 3 * 20L);
    }
    
    public void showFailureBlock(Player player, Inventory inventory) {
        ItemStack failureBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = failureBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.RED + "Enhancement Failed"));
            failureBlock.setItemMeta(meta);
        }
        inventory.setItem(25, failureBlock);
    
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateSuccessChancePanel(inventory);
        }, 3 * 20L);
    }

    // Add this new method inside the EnhanceGUIListener class
    public void updateSuccessChancePanel(Inventory inventory) {
        ItemStack itemToEnhance = inventory.getItem(19);
        if (itemToEnhance != null && itemToEnhance.getType() != Material.AIR) {
            ItemMeta itemMeta = itemToEnhance.getItemMeta();
            NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
            int currentLevel = itemMeta.getPersistentDataContainer().getOrDefault(enhancementLevelKey, PersistentDataType.INTEGER, 0);
            int nextLevel = currentLevel + 1;
            double successChance = Utils.getSuccessChance(plugin, nextLevel);
            inventory.setItem(16, EnhanceGUI.createSuccessChancePanel(successChance));

            // if item level is 20 or higher, set the enhance button to be the max enhancement achieved button
            if (currentLevel >= 20) {
                inventory.setItem(25, EnhanceGUI.createMaxEnhancementAchievedButton());
            }
        } else {
            inventory.setItem(16, EnhanceGUI.createSuccessChancePanel(0));
        }
    }

    public static @Nullable ItemStack createMaxEnhancementAchievedButton() {
        ItemStack maxEnhancementAchievedButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = maxEnhancementAchievedButton.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Max Enhancement Achieved");
            maxEnhancementAchievedButton.setItemMeta(meta);
        }
    
        return maxEnhancementAchievedButton;
    }
}
