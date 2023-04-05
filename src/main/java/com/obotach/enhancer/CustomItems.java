package com.obotach.enhancer;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

public class CustomItems {
    public static ItemStack createBlackStoneWeapon() {
        ItemStack blackStoneWeapon = new ItemStack(Material.COAL, 1);
        ItemMeta meta = blackStoneWeapon.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Black Stone (Weapon)"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(CustomItemKeys.BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER, 1);
            blackStoneWeapon.setItemMeta(meta);
        }

        return blackStoneWeapon;
    }

    public static ItemStack createBlackStoneArmor() {
        ItemStack blackStoneArmor = new ItemStack(Material.CHARCOAL, 1);
        ItemMeta meta = blackStoneArmor.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Black Stone (Armor)"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(CustomItemKeys.BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER, 1);
            blackStoneArmor.setItemMeta(meta);
        }

        return blackStoneArmor;
    }

    public static ItemStack createConcentratedMagicalBlackStoneWeapon() {
        ItemStack concentratedMagicalBlackStoneWeapon = new ItemStack(Material.RED_DYE, 1);
        ItemMeta meta = concentratedMagicalBlackStoneWeapon.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("§cConcentrated Magical Black Stone (Weapon)"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(CustomItemKeys.CONCENTRATED_MAGICAL_BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER, 1);
            concentratedMagicalBlackStoneWeapon.setItemMeta(meta);
        }

        return concentratedMagicalBlackStoneWeapon;
    }

    public static ItemStack createConcentratedMagicalBlackStoneArmor() {
        ItemStack concentratedMagicalBlackStoneArmor = new ItemStack(Material.CYAN_DYE, 1);
        ItemMeta meta = concentratedMagicalBlackStoneArmor.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("§cConcentrated Magical Black Stone (Armor)"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(CustomItemKeys.CONCENTRATED_MAGICAL_BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER, 1);
            concentratedMagicalBlackStoneArmor.setItemMeta(meta);
        }

        return concentratedMagicalBlackStoneArmor;
    }

    public static ItemStack createProtectionRune() {
        ItemStack protectionRune = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = protectionRune.getItemMeta();
    
        if (meta != null) {
            meta.displayName(Component.text("§eProtection Rune"));
            meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "protection_rune"), PersistentDataType.INTEGER, 1);
            protectionRune.setItemMeta(meta);
        }
    
        return protectionRune;
    }
    

    public class CustomItemKeys {
        public static final NamespacedKey BLACK_STONE_WEAPON_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "black_stone_weapon");
        public static final NamespacedKey BLACK_STONE_ARMOR_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "black_stone_armor");
        
        public static final NamespacedKey CONCENTRATED_MAGICAL_BLACK_STONE_WEAPON_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "concentrated_magical_black_stone_weapon");
        public static final NamespacedKey CONCENTRATED_MAGICAL_BLACK_STONE_ARMOR_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "concentrated_magical_black_stone_armor");

        public static final NamespacedKey PROTECTION_RUNE_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "protection_rune");
    }
}
