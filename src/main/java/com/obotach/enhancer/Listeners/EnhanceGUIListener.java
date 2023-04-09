package com.obotach.enhancer.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.obotach.enhancer.EnhanceGUI;
import com.obotach.enhancer.Enhancing;
import com.obotach.enhancer.Utils;
import com.obotach.enhancer.CustomItems.CustomItemKeys;
import com.obotach.enhancer.EnhancementInfo;

public class EnhanceGUIListener implements Listener {
    public static final NamespacedKey ENHANCEMENT_LEVEL_KEY = new NamespacedKey(Enhancing.getPlugin(Enhancing.class), "enhancement_level");
    // plugin preifx from config
    private final String prefix = Enhancing.getPlugin(Enhancing.class).getConfig().getString("plugin-prefix");
    private final Map<UUID, Integer> enhancementTasks;

    private final Enhancing plugin;
    private EnhanceGUI enhanceGUI;

    public EnhanceGUIListener(Enhancing plugin, EnhanceGUI enhanceGUI) {
        this.plugin = plugin;
        this.enhanceGUI = enhanceGUI;
        this.enhancementTasks = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Component viewTitle = event.getView().title();

        if (viewTitle == null || !viewTitle.equals(Component.text(EnhanceGUI.INVENTORY_TITLE))) {
            return;
        }

        // Check if the player has an ongoing enhancement
        if (isPlayerEnhancing(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        int clickedSlot = event.getSlot();
        Inventory clickedInventory = event.getClickedInventory();

        // If the clicked inventory is not the EnhanceGUI inventory, allow the event
        if (clickedInventory == null || !clickedInventory.equals(event.getView().getTopInventory())) {
            return;
        }

        // Cancel the click event to prevent players from taking items from the GUI, except in slots 19, 22, 34
        if (clickedSlot != 19 && clickedSlot != 22 && clickedSlot != 34) {
            event.setCancelled(true);
        }

        InventoryAction action = event.getAction();

        // code below updates the enhance button when the item or black stone is placed in the slot so we always have latest enchant success rate
        if (clickedSlot == 19 || clickedSlot == 22 || action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.SWAP_WITH_CURSOR || action == InventoryAction.COLLECT_TO_CURSOR || action == InventoryAction.HOTBAR_MOVE_AND_READD || action == InventoryAction.HOTBAR_SWAP) {
            // Call updateEnhanceButton() when the item or black stone is placed in the slot
            enhanceGUI.updateSuccessChancePanel(event.getClickedInventory());
        }

        // Check if the clicked slot is the enhance button
        if (event.getSlot() == 25) {
            ItemStack itemToEnhance = event.getClickedInventory().getItem(19);
            ItemStack blackStone = event.getClickedInventory().getItem(22);

            if (itemToEnhance == null || itemToEnhance.getType() == Material.AIR || blackStone == null || blackStone.getType() == Material.AIR) {
                return;
            }

            ItemMeta blackStoneMeta = blackStone.getItemMeta();
            if (blackStoneMeta == null) {
                return;
            }

            boolean isWeapon = Utils.isWeapon(itemToEnhance);
            boolean isArmor = Utils.isArmor(itemToEnhance);
            int enhancementLevel = itemToEnhance.getItemMeta().getPersistentDataContainer().getOrDefault(ENHANCEMENT_LEVEL_KEY, PersistentDataType.INTEGER, 0) + 1;

            if (isWeapon && blackStoneMeta.getPersistentDataContainer().has(CustomItemKeys.BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER) && enhancementLevel <= 15) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 19, 22);
            } else if (isArmor && blackStoneMeta.getPersistentDataContainer().has(CustomItemKeys.BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER) && enhancementLevel <= 15) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 19, 22);
            } else if (isWeapon && blackStoneMeta.getPersistentDataContainer().has(CustomItemKeys.CONCENTRATED_MAGICAL_BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER) && enhancementLevel >= 16 && enhancementLevel <= 20) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 19, 22);
            } else if (isArmor && blackStoneMeta.getPersistentDataContainer().has(CustomItemKeys.CONCENTRATED_MAGICAL_BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER) && enhancementLevel >= 16 && enhancementLevel <= 20) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 19, 22);
            } else if ( enhancementLevel >= 20) {
                clickedInventory.setItem(25, EnhanceGUI.createMaxEnhancementAchievedButton());
            } else {
                showErrorInGUI(clickedInventory, enhancementLevel);
            }
        }
    }

    private void startEnhancementAnimation(Player player, Inventory inventory, int itemSlot, int blackStoneSlot) {
        ItemStack protectionStone = inventory.getItem(34);
        
        //check if slot 34 is empty if not check if it is a Protection Rune. If it is not a protection rune show error
        if (protectionStone != null && protectionStone.getType() != Material.AIR && !protectionStone.getItemMeta().getPersistentDataContainer().has(CustomItemKeys.PROTECTION_RUNE_KEY, PersistentDataType.INTEGER)) {
            //return itenm to player and remove it from inventory
            player.getInventory().addItem(protectionStone);
            inventory.setItem(34, null);
            showErrorInGUI(inventory, 0);
            return;
        }
        
        Material[] countdownBlocks = {Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.GREEN_CONCRETE};
    
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int countdown = 5;
    
            @Override
            public void run() {
                if (countdown > 0) {
                    // Change the block and play the sound
                    inventory.setItem(25, Utils.createCountdownItem(countdownBlocks[countdown - 1], countdown));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    countdown--;
                } else {
                    // Cancel the task and enhance the item
                    Bukkit.getScheduler().cancelTask(enhancementTasks.get(player.getUniqueId()));
                    enhancementTasks.remove(player.getUniqueId());
                    enhanceItem(player, inventory, itemSlot, blackStoneSlot);
                }
            }
        }, 0L, 20L);
    
        // Store the task ID for the player
        enhancementTasks.put(player.getUniqueId(), taskId);
    }

    private void showErrorInGUI(Inventory inventory, int enhancementLevel) {
        ItemStack errorBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = errorBlock.getItemMeta();
        if (meta != null) {
            String errorMessage;
            if (enhancementLevel <= 15) {
                errorMessage = ChatColor.RED + "You must use the correct Black Stone for the item you want to enhance.";
            } else {
                errorMessage = ChatColor.RED + "You must use a Concentrated Magical Black Stone for enhancement levels 16-20.";
            }
            meta.displayName(Component.text(errorMessage));
            errorBlock.setItemMeta(meta);
        }
        inventory.setItem(25, errorBlock);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            inventory.setItem(25, enhanceGUI.createEnhanceButton());
        }, 3 * 20L);
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Component viewTitle = event.getView().title();

        if (viewTitle == null || !viewTitle.equals(Component.text(EnhanceGUI.INVENTORY_TITLE))) {
            return;
        }

        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        ItemStack item = inventory.getItem(19);
        ItemStack blackStone = inventory.getItem(22);
        ItemStack protectionRune = inventory.getItem(34);

        if (item != null) {
            player.getInventory().addItem(item);
        }

        if (blackStone != null) {
            player.getInventory().addItem(blackStone);
        }

        if (protectionRune != null) {
            player.getInventory().addItem(protectionRune);
        }

        UUID playerUUID = event.getPlayer().getUniqueId();
        if (enhancementTasks.containsKey(playerUUID)) {
            Bukkit.getScheduler().cancelTask(enhancementTasks.get(playerUUID));
            enhancementTasks.remove(playerUUID);
        }
    }

    private void enhanceItem(Player player, Inventory inventory, int itemSlot, int blackStoneSlot) {
        ItemStack itemToEnhance = inventory.getItem(itemSlot);
        ItemStack blackStone = inventory.getItem(blackStoneSlot);
        ItemStack protectionStone = inventory.getItem(34);
        ItemMeta itemMeta = itemToEnhance.getItemMeta();

        boolean hasProtectionStone = protectionStone != null && protectionStone.getType() != Material.AIR && protectionStone.getItemMeta().getPersistentDataContainer().has(CustomItemKeys.PROTECTION_RUNE_KEY, PersistentDataType.INTEGER);

        NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
        int currentLevel = itemMeta.getPersistentDataContainer().getOrDefault(enhancementLevelKey, PersistentDataType.INTEGER, 0);
        double successChance = Utils.getSuccessChance(plugin, currentLevel);

        Random random = new Random();
        boolean success = random.nextDouble() * 100 < successChance;

        int nextLevel = currentLevel + 1;
        EnhancementInfo enhanceName = Utils.getEnhancementInfo(nextLevel);
        if (success) {
            itemMeta.getPersistentDataContainer().set(enhancementLevelKey, PersistentDataType.INTEGER, nextLevel);
            if (nextLevel > 15) {
                itemMeta.displayName(Utils.getEnhancementName(nextLevel));
                Utils.betterBroadcast(prefix + ChatColor.RED + player.getName() +  ChatColor.GREEN + " Succesfully Enhanced " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " from " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel)) + " to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(nextLevel)));
            } else {
                itemMeta.displayName(Component.text(enhanceName.getEnhanceColor() + "+" + enhanceName.getEnhanceName()));
                Utils.betterBroadcast(prefix + ChatColor.AQUA + player.getName() +  ChatColor.GREEN + " Succesfully Enhanced " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " from " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel)) + ChatColor.GREEN + " to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(nextLevel)));
            }
            itemToEnhance.setItemMeta(itemMeta); // Set the new meta before applying enchantments
            Utils.applyEnchantments(plugin, itemToEnhance, nextLevel);
            enhanceGUI.updateSuccessChancePanel(inventory);
            enhanceGUI.showSuccessBlock(player, inventory);
            spawnSuccessParticles(player);
        } else {
            if (currentLevel >= 16 && currentLevel <= 20 && !hasProtectionStone) {
                // Downgrade the item by one level
                currentLevel--;
                itemMeta.getPersistentDataContainer().set(enhancementLevelKey, PersistentDataType.INTEGER, currentLevel);
                itemMeta.displayName(Utils.getEnhancementName(currentLevel));
                itemToEnhance.setItemMeta(itemMeta);
    
                // Announce the downgrade to users
                Utils.betterBroadcast(prefix + ChatColor.YELLOW + player.getName() + ChatColor.RED + " Failed to Enhance " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " from " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel+1)) + ChatColor.RED + " to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(nextLevel)) + ChatColor.GRAY + " and downgraded to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel)));
            } else if (currentLevel >= 16 && currentLevel <= 20 && hasProtectionStone) {
                // Announce the failure to enhance with protection to users
                Utils.betterBroadcast(prefix + ChatColor.YELLOW + player.getName() + ChatColor.RED + " Failed to Enhance " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " from " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel)) + " to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(nextLevel)) + ChatColor.GRAY + " but item is protected by Protection Stone.");
            } else {
                Utils.betterBroadcast(prefix + ChatColor.YELLOW + player.getName() + ChatColor.RED + " Failed to Enhance " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " from " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(currentLevel)) + " to " + LegacyComponentSerializer.legacySection().serialize(Utils.getEnhancementName(nextLevel)));
            }
    
            enhanceGUI.showFailureBlock(player, inventory);
        }
    
        if (blackStone.getAmount() > 1) {
            blackStone.setAmount(blackStone.getAmount() - 1);
        } else {
            inventory.setItem(blackStoneSlot, null);
        }

        if (hasProtectionStone) {
            if (protectionStone.getAmount() > 1) {
                protectionStone.setAmount(protectionStone.getAmount() - 1);
            } else {
                inventory.setItem(34, null);
            }
        }
    }

    private boolean isPlayerEnhancing(UUID playerUUID) {
        return enhancementTasks.containsKey(playerUUID);
    }

    private void spawnSuccessParticles(Player player) {
        for (int i = 0; i < 100; i++) {
            double offsetX = (Math.random() * 2) - 1;
            double offsetY = (Math.random() * 2) - 1;
            double offsetZ = (Math.random() * 2) - 1;
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0), 0, offsetX, offsetY, offsetZ, 0.1);
        }
    }
}