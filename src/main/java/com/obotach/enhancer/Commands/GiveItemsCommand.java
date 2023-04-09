package com.obotach.enhancer.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.obotach.enhancer.CustomItems;
import com.obotach.enhancer.Enhancing;

public class GiveItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("enhancing.giveblackstone")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
        
        Player player = (Player) sender;

        if (args.length == 3) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage("Invalid player. Please enter a valid player name.");
                return true;
            }

            String itemType = args[1].toLowerCase();
            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount. Please enter a valid number.");
                return true;
            }

            if (amount < 1) {
                player.sendMessage("The amount must be at least 1.");
                return true;
            }

            ItemStack itemToGive = null;

            if (itemType.equals("weapon")) {
                itemToGive = CustomItems.createBlackStoneWeapon();
            } else if (itemType.equals("armor")) {
                itemToGive = CustomItems.createBlackStoneArmor();
            } else if (itemType.equals("cweapon")) {
                itemToGive = CustomItems.createConcentratedMagicalBlackStoneWeapon();
            } else if (itemType.equals("carmor")) {
                itemToGive = CustomItems.createConcentratedMagicalBlackStoneArmor();
            } else if (itemType.equals("pstone")) {
                itemToGive = CustomItems.createProtectionRune();
            } else {
                sender.sendMessage("Usage: /giveblackstone <player> <weapon|armor|cweapon|carmor> <amount>");
                return true;
            }

            itemToGive.setAmount(amount);
            targetPlayer.getInventory().addItem(itemToGive);
            targetPlayer.sendMessage("You have received " + amount + " Black Stone(s) (" + itemType + ").");
            player.sendMessage("You have given " + amount + " Black Stone(s) (" + itemType + ") to " + targetPlayer.getName() + ".");
        } else {
            sender.sendMessage("Usage: /giveblackstone <player> <weapon|armor|cweapon|carmor> <amount>");
        }

        return true;
    }
}
