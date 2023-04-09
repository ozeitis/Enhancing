package com.obotach.enhancer;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class Utils {

    public static void betterBroadcast(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    
    public static boolean isWeapon(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE") || type.name().endsWith("_BOW") || type.name().endsWith("_CROSSBOW");
    }

    public static boolean isArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET") || type.name().endsWith("_CHESTPLATE") || type.name().endsWith("_LEGGINGS") || type.name().endsWith("_BOOTS");
    }

    public static double getSuccessChance(Enhancing plugin, int currentLevel) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection(String.valueOf(currentLevel));
        if (config == null) {
            return 0;
        }
    
        double successChance = config.getDouble("success_chance");
        if (successChance <= 0) {
            return 0;
        }
    
        return successChance;
    }

    public static Component getEnhancementName(int enhancementLevel) {
        if (enhancementLevel > 15) {
            return Component.text(Utils.getEnhancementInfo(enhancementLevel).getEnhanceColor() + "" + ChatColor.BOLD + "" + Utils.getEnhancementInfo(enhancementLevel).getEnhanceName());
        } else {
            return Component.text(Utils.getEnhancementInfo(enhancementLevel).getEnhanceColor() + "+" + Utils.getEnhancementInfo(enhancementLevel).getEnhanceName());
        }
    }

    public static void applyEnchantments(Enhancing plugin, ItemStack item, int enhancementLevel) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection(String.valueOf(enhancementLevel));
        if (config == null) {
            return;
        }
    
        if (Utils.isWeapon(item)) {
            List<String> weaponEnchants = config.getStringList("weapon");
            applyEnchantmentList(item, weaponEnchants);
        } else if (Utils.isArmor(item)) {
            List<String> armorEnchants = config.getStringList("armor");
            applyEnchantmentList(item, armorEnchants);
        }
    }
    
    private static void applyEnchantmentList(ItemStack item, List<String> enchantmentList) {
        for (String enchantmentString : enchantmentList) {
            String[] enchantmentData = enchantmentString.split(":");
            String enchantmentName = enchantmentData[0];
            int enchantmentLevel = Integer.parseInt(enchantmentData[1]);
    
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
            if (enchantment != null) {
                item.addUnsafeEnchantment(enchantment, enchantmentLevel);
            }
        }
    }

    public static ItemStack createCountdownItem(Material material, int countdown) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.AQUA + "Enhancing in " + ChatColor.BOLD + countdown + " seconds"));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static EnhancementInfo getEnhancementInfo(int nextLevel) {
        String enhanceName;
        ChatColor enhanceColor;
    
        switch (nextLevel) {
            case 16:
                enhanceName = "PRI";
                enhanceColor = ChatColor.GREEN;
                break;
            case 17:
                enhanceName = "DUO";
                enhanceColor = ChatColor.AQUA;
                break;
            case 18:
                enhanceName = "TRI";
                enhanceColor = ChatColor.GOLD;
                break;
            case 19:
                enhanceName = "TET";
                enhanceColor = ChatColor.YELLOW;
                break;
            case 20:
                enhanceName = "PEN";
                enhanceColor = ChatColor.RED;
                break;
            default:
                enhanceName = String.valueOf(nextLevel);
                enhanceColor = ChatColor.GREEN;
                break;
        }
    
        return new EnhancementInfo(enhanceName, enhanceColor);
    }

    public static boolean isBlackStone(ItemStack item) {
        if (item == null || item.getType() != Material.BLACKSTONE) {
            return false;
        }
    
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
    
        return ChatColor.stripColor(meta.getDisplayName()).equals("Black Stone");
    }
    
    public static boolean isProtectionRune(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }
    
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
    
        return ChatColor.stripColor(meta.getDisplayName()).equals("Protection Rune");
    }
    
}
