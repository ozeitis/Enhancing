package com.obotach.enhancer.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.obotach.enhancer.Enhancing;
import com.obotach.enhancer.Listeners.EnhanceGUIListener;

import net.kyori.adventure.text.Component;

public class EnhanceItemCommand implements CommandExecutor {
    private final EnhanceGUIListener enhanceGUIListener;
    private final Enhancing plugin;

    public EnhanceItemCommand(Enhancing plugin, EnhanceGUIListener enhanceGUIListener) {
        this.plugin = plugin;
        this.enhanceGUIListener = enhanceGUIListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /enhanceitem <level>");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid level. Please enter a valid number.");
            return true;
        }

        if (level < 0 || level > 20) {
            sender.sendMessage("Invalid level. The enhancement level must be between 0 and 20.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage("You must hold an item in your main hand to enhance it.");
            return true;
        }

        if (!enhanceGUIListener.isWeapon(item) && !enhanceGUIListener.isArmor(item)) {
            sender.sendMessage("You can only enhance weapons and armor.");
            return true;
        }

        ItemMeta itemMeta = item.getItemMeta();
        NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
        itemMeta.getPersistentDataContainer().set(enhancementLevelKey, PersistentDataType.INTEGER, level);
        item.setItemMeta(itemMeta);

        String enhanceName;
        ChatColor enhanceColor;
        switch (level) {
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
                enhanceName = String.valueOf(level);
                enhanceColor = ChatColor.GREEN;
                break;
        }
        itemMeta.getPersistentDataContainer().set(enhancementLevelKey, PersistentDataType.INTEGER, level);
        if (level > 15) {
            itemMeta.displayName(Component.text(enhanceColor + "" + ChatColor.BOLD + "" + enhanceName));
        } else {
            itemMeta.displayName(Component.text(enhanceColor + "+" + enhanceName));
        }
        item.setItemMeta(itemMeta); // Set the new meta before applying enchantment
        enhanceGUIListener.applyEnchantments(item, level);
        sender.sendMessage("Item enhanced to level " + level + ".");
        return true;
    }
}
