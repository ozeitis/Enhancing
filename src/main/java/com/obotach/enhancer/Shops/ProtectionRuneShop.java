package com.obotach.enhancer.Shops;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.obotach.enhancer.CustomItems;
import com.obotach.enhancer.Enhancing;

public class ProtectionRuneShop implements CommandExecutor, Listener {
    private static final String SHOP_NAME = "§6Protection Rune Shop";
    private static final int SHOP_SIZE = 27;
    private static final int PROTECTION_RUNE_SLOT = 13;
    private int protectionStoneEXPCost;

    public ProtectionRuneShop(int protectionStoneEXPCost) {
        this.protectionStoneEXPCost = protectionStoneEXPCost;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        openShop(player);
        return true;
    }

    public void openShop(Player player) {
        Inventory shop = Bukkit.createInventory(null, SHOP_SIZE, SHOP_NAME);

        // Fill shop with black glass panes
        ItemStack blackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackGlassPaneMeta = blackGlassPane.getItemMeta();
        if (blackGlassPaneMeta != null) {
            blackGlassPaneMeta.setDisplayName("§0");
            blackGlassPane.setItemMeta(blackGlassPaneMeta);
        }
        for (int i = 0; i < SHOP_SIZE; i++) {
            shop.setItem(i, blackGlassPane);
        }

        // Add protection rune in the middle
        ItemStack protectionRune = CustomItems.createProtectionRune();
        ItemMeta protectionRuneMeta = protectionRune.getItemMeta();
        if (protectionRuneMeta != null) {
            protectionRuneMeta.setLore(List.of("§7Click to buy", "§eCost: "+protectionStoneEXPCost+" levels for 1 protection runes"));
            protectionRune.setItemMeta(protectionRuneMeta);
        }
        shop.setItem(PROTECTION_RUNE_SLOT, protectionRune);

        player.openInventory(shop);
    }

    @EventHandler
public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
        return;
    }

    Player player = (Player) event.getWhoClicked();

    if (event.getView().getTitle().equals(SHOP_NAME)) {
        event.setCancelled(true);

        if (event.getSlot() == PROTECTION_RUNE_SLOT) {
            int requiredLevels = protectionStoneEXPCost;

            if (player.getLevel() >= requiredLevels) {
                player.setLevel(player.getLevel() - requiredLevels);
                ItemStack protectionRunes = CustomItems.createProtectionRune();
                protectionRunes.setAmount(1);
                player.getInventory().addItem(protectionRunes);

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage("§aYou have successfully purchased a Protection Rune!");

                // Show success block
                ItemStack successItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta successMeta = successItem.getItemMeta();
                if (successMeta != null) {
                    successMeta.setDisplayName("§aPurchase successful!");
                    successItem.setItemMeta(successMeta);
                }
                event.getClickedInventory().setItem(PROTECTION_RUNE_SLOT, successItem);

                // Schedule a task to revert the success item back to the protection rune after a delay
                Bukkit.getScheduler().runTaskLater(Enhancing.getPlugin(Enhancing.class), () -> {
                    ItemStack protectionRune = CustomItems.createProtectionRune();
                    ItemMeta protectionRuneMeta = protectionRune.getItemMeta();
                    if (protectionRuneMeta != null) {
                        protectionRuneMeta.setLore(List.of("§7Click to buy", "§eCost: "+protectionStoneEXPCost+" levels for a Protection Rune"));
                        protectionRune.setItemMeta(protectionRuneMeta);
                    }
                    event.getClickedInventory().setItem(PROTECTION_RUNE_SLOT, protectionRune);
                }, 60L); // 60 ticks (3 seconds) delay
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);

                ItemStack errorItem = new ItemStack(Material.BARRIER);
                ItemMeta errorMeta = errorItem.getItemMeta();
                if (errorMeta != null) {
                    errorMeta.setDisplayName("§cError: Insufficient levels");
                    errorMeta.setLore(List.of("§7You need "+protectionStoneEXPCost+" levels to buy a Protection Rune."));
                    errorItem.setItemMeta(errorMeta);
                }
                event.getClickedInventory().setItem(PROTECTION_RUNE_SLOT, errorItem);

                // Schedule a task to revert the error item back to the protection rune after a delay
                Bukkit.getScheduler().runTaskLater(Enhancing.getPlugin(Enhancing.class), () -> {
                    ItemStack protectionRune = CustomItems.createProtectionRune();
                    ItemMeta protectionRuneMeta = protectionRune.getItemMeta();
                    if (protectionRuneMeta != null) {
                        protectionRuneMeta.setLore(List.of("§7Click to buy", "§eCost: "+protectionStoneEXPCost+" levels for a Protection Rune"));
                        protectionRune.setItemMeta(protectionRuneMeta);
                    }
                    event.getClickedInventory().setItem(PROTECTION_RUNE_SLOT, protectionRune);
                }, 60L); // 60 ticks (3 seconds) delay
                }
            }
        }
    }
}
    