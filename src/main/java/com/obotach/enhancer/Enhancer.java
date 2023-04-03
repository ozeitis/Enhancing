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

import net.md_5.bungee.api.ChatColor;

public class Enhancer extends JavaPlugin implements Listener {

    private static Enhancer instance;

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

        getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        getServer().getPluginManager().registerEvents(new EnhanceGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new DisableEnchantingListener(), this);

        getCommand("enhance").setExecutor(new EnhanceCommandExecutor());
        getCommand("giveblackstone").setExecutor(new GiveBlackStoneCommand(this));
        getCommand("reloadconfig").setExecutor(new ReloadCommandExecutor(this));

        //anounce plugin is enabled
        getLogger().info("Enhancer has been enabled!");
    }

    public static Enhancer getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public class ReloadCommandExecutor implements CommandExecutor {

        private final Enhancer plugin;
    
        public ReloadCommandExecutor(Enhancer plugin) {
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