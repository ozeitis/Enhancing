package com.obotach.enhancer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveBlackStoneCommand implements CommandExecutor {

    private final Enhancer plugin;

    public GiveBlackStoneCommand(Enhancer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2) {
            String itemType = args[0].toLowerCase();
            int amount;

            try {
                amount = Integer.parseInt(args[1]);
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
                blackStone = BlackStones.createBlackStoneWeapon();
            } else if (itemType.equals("armor")) {
                blackStone = BlackStones.createBlackStoneArmor();
            } else if (itemType.equals("cweapon")) {
                blackStone = BlackStones.createConcentratedMagicalBlackStoneWeapon();
            } else if (itemType.equals("carmor")) {
                blackStone = BlackStones.createConcentratedMagicalBlackStoneArmor();
            } else {
                player.sendMessage("Usage: /giveblackstone <weapon|armor|cweapon|carmor> <amount>");
                return true;
            }

            blackStone.setAmount(amount);
            player.getInventory().addItem(blackStone);
            player.sendMessage("You have received " + amount + " Black Stone(s) (" + itemType + ").");
        } else {
            player.sendMessage("Usage: /giveblackstone <weapon|armor|cweapon|carmor> <amount>");
        }

        return true;
    }
}
