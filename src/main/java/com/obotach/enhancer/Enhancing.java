package com.obotach.enhancer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.obotach.enhancer.Commands.EnhanceCommandExecutor;
import com.obotach.enhancer.Commands.EnhanceItemCommand;
import com.obotach.enhancer.Commands.GiveItemsCommand;
import com.obotach.enhancer.Commands.RepairCommand;
import com.obotach.enhancer.Listeners.BlockBreakListener;
import com.obotach.enhancer.Listeners.DisableEnchantingListener;
import com.obotach.enhancer.Listeners.EnhanceGUIListener;
import com.obotach.enhancer.Listeners.MobDeathListener;
import com.obotach.enhancer.Shops.ProtectionRuneShop;

import net.md_5.bungee.api.ChatColor;

public class Enhancing extends JavaPlugin implements Listener {

    private static Enhancing instance;

    @Override
    public void onEnable() {
        instance = this;
        // Create config file if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                getLogger().info("Created config file.");

                // Write example data to config file
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                // add black-stone-drop-chance to config
                config.set("plugin-prefix", ChatColor.GOLD + "[Enhancing] " + ChatColor.RESET);
                config.set("black-stone-drop-chance", 1);
                config.set("concentrated-black-stone-drop-chance", 0.5);
                config.set("memory-fragment-drop-chance", 0.5);
                config.set("protection-stone-drop-chance", 0.1);
                config.set("protection-stone-exp-cost", 100);
                for (int i = 0; i <= 19; i++) {
                    String path = i + ".weapon";
                    List<String> enchants = Arrays.asList("sharpness:1", "unbreaking:1");
                    config.set(path, enchants);

                    path = i + ".armor";
                    enchants = Arrays.asList("protection:1", "unbreaking:1");
                    config.set(path, enchants);
                    config.set(i + ".success_chance", 100);
                }
                config.save(configFile);
                getLogger().info("Wrote example data to config file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Bukkit.getPluginManager().registerEvents(this, this);
        double blackStoneDropChance = getConfig().getDouble("black-stone-drop-chance");
        double concentratedBlackStoneDropChance = getConfig().getDouble("concentrated-black-stone-drop-chance");
        double memoryFragmentDropChance = getConfig().getDouble("memory-fragment-drop-chance");
        double protectionStoneDropChance = getConfig().getDouble("protection-stone-drop-chance");
        int protectionStoneEXPCost = getConfig().getInt("protection-stone-exp-cost");
        getServer().getPluginManager().registerEvents(new MobDeathListener(blackStoneDropChance, concentratedBlackStoneDropChance, protectionStoneDropChance), this);
        EnhanceGUI enhanceGUI = new EnhanceGUI(this);
        getServer().getPluginManager().registerEvents(new EnhanceGUIListener(this, enhanceGUI), this);
        getServer().getPluginManager().registerEvents(new DisableEnchantingListener(), this);
        getServer().getPluginManager().registerEvents(new ProtectionRuneShop(protectionStoneEXPCost), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(memoryFragmentDropChance), this);

        getCommand("enhance").setExecutor(new EnhanceCommandExecutor());
        getCommand("giveblackstone").setExecutor(new GiveItemsCommand());
        getCommand("enhanceitem").setExecutor(new EnhanceItemCommand(this));
        getCommand("reloadconfig").setExecutor(new ReloadCommandExecutor(this));
        getCommand("protectionshop").setExecutor(new ProtectionRuneShop(protectionStoneEXPCost));
        getCommand("erepair").setExecutor(new RepairCommand(this));

        //anounce plugin is enabled and say what version it is and what drop chance is
        String version = getDescription().getVersion();
        getLogger().info("Enhancing plugin enabled (version " + version + ")");
        getLogger().info("Black stone drop chance: " + blackStoneDropChance);
        getLogger().info("Concentrated black stone drop chance: " + concentratedBlackStoneDropChance);

    }

    public static Enhancing getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public class ReloadCommandExecutor implements CommandExecutor {

        private final Enhancing plugin;
    
        public ReloadCommandExecutor(Enhancing plugin) {
            this.plugin = plugin;
        }
    
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (command.getName().equalsIgnoreCase("reloadconfig")) {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
                return true;
            }
            return false;
        }
    }
}