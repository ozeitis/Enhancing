package com.obotach.enhancer.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.obotach.enhancer.EnhanceGUI;

public class EnhanceCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
        
        if (!sender.hasPermission("enhancing.enhance")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            EnhanceGUI.openEnhanceGUI(player);
        } else {
            sender.sendMessage("This command can only be used by players.");
        }

        return true;
    }
}
