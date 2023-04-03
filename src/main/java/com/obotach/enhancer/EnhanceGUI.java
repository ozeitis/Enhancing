package com.obotach.enhancer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

public class EnhanceGUI {

    public static final String INVENTORY_TITLE = ChatColor.BLUE + "Enhance Item";

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
    
        enhanceInventory.setItem(16, EnhanceGUI.createEnhanceButton(0));
    
        return enhanceInventory;
    }
    
    

    public static void openEnhanceGUI(Player player) {
        player.openInventory(createEnhanceInventory());
    }

    private static ItemStack createPlaceholderItem(String description) {
        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
    
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + description);
            placeholder.setItemMeta(meta);
        }
    
        return placeholder;
    }
    
    static ItemStack createEnhanceButton(double successChance) {
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
