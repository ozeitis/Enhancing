package com.obotach.enhancer;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

public class BlackStones {
    public static ItemStack createBlackStoneWeapon() {
        ItemStack blackStoneWeapon = new ItemStack(Material.COAL, 1);
        ItemMeta meta = blackStoneWeapon.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Black Stone (Weapon)"));
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(BlackStoneKeys.BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER, 1);
            blackStoneWeapon.setItemMeta(meta);
        }

        return blackStoneWeapon;
    }

    public static ItemStack createBlackStoneArmor() {
        ItemStack blackStoneArmor = new ItemStack(Material.CHARCOAL, 1);
        ItemMeta meta = blackStoneArmor.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Black Stone (Armor)"));
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(BlackStoneKeys.BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER, 1);
            blackStoneArmor.setItemMeta(meta);
        }

        return blackStoneArmor;
    }

    public class BlackStoneKeys {
        public static final NamespacedKey BLACK_STONE_WEAPON_KEY = new NamespacedKey(Enhancer.getPlugin(Enhancer.class), "black_stone_weapon");
        public static final NamespacedKey BLACK_STONE_ARMOR_KEY = new NamespacedKey(Enhancer.getPlugin(Enhancer.class), "black_stone_armor");
    }

}
