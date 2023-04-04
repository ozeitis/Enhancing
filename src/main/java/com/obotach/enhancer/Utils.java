package com.obotach.enhancer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Utils {
    
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
}
