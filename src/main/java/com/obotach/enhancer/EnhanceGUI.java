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
        Inventory enhanceInventory = Bukkit.createInventory(null, 27, titleComponent);
    
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        if (placeholderMeta != null) {
            placeholderMeta.setDisplayName(ChatColor.GRAY + " ");
            placeholder.setItemMeta(placeholderMeta);
        }
    
        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16) {
                enhanceInventory.setItem(i, placeholder);
            }
        }
    
        ItemStack enhanceButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta enhanceButtonMeta = enhanceButton.getItemMeta();
        if (enhanceButtonMeta != null) {
            enhanceButtonMeta.setDisplayName(ChatColor.GREEN + "Enhance");
            enhanceButton.setItemMeta(enhanceButtonMeta);
        }
    
        enhanceInventory.setItem(16, createEnhanceButton(0));
    
        return enhanceInventory;
    }
    
    

    public static void openEnhanceGUI(Player player) {
        player.openInventory(createEnhanceInventory());
    }

    private  ItemStack createPlaceholderItem(String description) {
        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + description);
            placeholder.setItemMeta(meta);
        }
    
        return placeholder;
    }
    
    public static ItemStack createEnhanceButton(double successChance) {
        ItemStack enhanceButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = enhanceButton.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Enhance");
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(ChatColor.GRAY + "Success Chance: " + ChatColor.AQUA + String.format("%.1f", successChance) + "%"));
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
        inventory.setItem(16, successBlock);
    
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateEnhanceButton(inventory);
        }, 3 * 20L);
    }
    
    public void showFailureBlock(Player player, Inventory inventory) {
        ItemStack failureBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = failureBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.RED + "Enhancement Failed"));
            failureBlock.setItemMeta(meta);
        }
        inventory.setItem(16, failureBlock);
    
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateEnhanceButton(inventory);
        }, 3 * 20L);
    }

    // Add this new method inside the EnhanceGUIListener class
    public void updateEnhanceButton(Inventory inventory) {
        ItemStack itemToEnhance = inventory.getItem(10);
        if (itemToEnhance != null && itemToEnhance.getType() != Material.AIR) {
            ItemMeta itemMeta = itemToEnhance.getItemMeta();
            NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
            int currentLevel = itemMeta.getPersistentDataContainer().getOrDefault(enhancementLevelKey, PersistentDataType.INTEGER, 0);
            int nextLevel = currentLevel + 1;
            double successChance = Utils.getSuccessChance(plugin, nextLevel);
            inventory.setItem(16, EnhanceGUI.createEnhanceButton(successChance));
        } else {
            inventory.setItem(16, EnhanceGUI.createEnhanceButton(0));
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
