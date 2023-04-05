package com.obotach.enhancer.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.obotach.enhancer.CustomItems;
import com.obotach.enhancer.Enhancing;

public class GiveBlackStoneCommand implements CommandExecutor {

    private final Enhancing plugin;

    public GiveBlackStoneCommand(Enhancing plugin) {
        this.plugin = plugin;
    }

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

            ItemStack blackStone = null;

            if (itemType.equals("weapon")) {
                blackStone = CustomItems.createBlackStoneWeapon();
            } else if (itemType.equals("armor")) {
                blackStone = CustomItems.createBlackStoneArmor();
            } else if (itemType.equals("cweapon")) {
                blackStone = CustomItems.createConcentratedMagicalBlackStoneWeapon();
            } else if (itemType.equals("carmor")) {
                blackStone = CustomItems.createConcentratedMagicalBlackStoneArmor();
            } else {
                sender.sendMessage("Usage: /giveblackstone <player> <weapon|armor|cweapon|carmor> <amount>");
                return true;
            }

            blackStone.setAmount(amount);
            targetPlayer.getInventory().addItem(blackStone);
            targetPlayer.sendMessage("You have received " + amount + " Black Stone(s) (" + itemType + ").");
            player.sendMessage("You have given " + amount + " Black Stone(s) (" + itemType + ") to " + targetPlayer.getName() + ".");
        } else {
            sender.sendMessage("Usage: /giveblackstone <player> <weapon|armor|cweapon|carmor> <amount>");
        }

        return true;
    }
}
