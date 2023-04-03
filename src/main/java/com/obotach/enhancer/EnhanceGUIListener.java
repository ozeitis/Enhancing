package com.obotach.enhancer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.obotach.enhancer.BlackStones.BlackStoneKeys;

public class EnhanceGUIListener implements Listener {
    private final Map<UUID, Integer> enhancementTasks;

    private final Enhancer plugin;
    public EnhanceGUIListener(Enhancer plugin) {
        this.plugin = plugin;
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

        // Cancel the click event to prevent players from taking items from the GUI, except in slots 10 and 13
        if (clickedSlot != 10 && clickedSlot != 13) {
            event.setCancelled(true);
        }

        InventoryAction action = event.getAction();
        if (clickedSlot == 10 || clickedSlot == 13 || action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.SWAP_WITH_CURSOR) {
            // Call updateEnhanceButton() when the item or black stone is placed in the slot
            updateEnhanceButton(event.getClickedInventory());
        }

        // Check if the clicked slot is the enhance button
        if (event.getSlot() == 16) {
            ItemStack itemToEnhance = event.getClickedInventory().getItem(10);
            ItemStack blackStone = event.getClickedInventory().getItem(13);

            if (itemToEnhance == null || itemToEnhance.getType() == Material.AIR || blackStone == null || blackStone.getType() == Material.AIR) {
                return;
            }

            ItemMeta blackStoneMeta = blackStone.getItemMeta();
            if (blackStoneMeta == null) {
                return;
            }

            if (isWeapon(itemToEnhance) && blackStoneMeta.getPersistentDataContainer().has(BlackStoneKeys.BLACK_STONE_WEAPON_KEY, PersistentDataType.INTEGER)) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 10, 13);
            } else if (isArmor(itemToEnhance) && blackStoneMeta.getPersistentDataContainer().has(BlackStoneKeys.BLACK_STONE_ARMOR_KEY, PersistentDataType.INTEGER)) {
                startEnhancementAnimation((Player) event.getWhoClicked(), event.getClickedInventory(), 10, 13);
            } else {
                showErrorInGUI(clickedInventory);
            }
        }
    }


    // Add this new method inside the EnhanceGUIListener class
    private void updateEnhanceButton(Inventory inventory) {
        ItemStack itemToEnhance = inventory.getItem(10);
        if (itemToEnhance != null && itemToEnhance.getType() != Material.AIR) {
            ItemMeta itemMeta = itemToEnhance.getItemMeta();
            NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
            int currentLevel = itemMeta.getPersistentDataContainer().getOrDefault(enhancementLevelKey, PersistentDataType.INTEGER, 0);
            int nextLevel = currentLevel + 1;
            double successChance = getSuccessChance(nextLevel);
            inventory.setItem(16, EnhanceGUI.createEnhanceButton(successChance));
        } else {
            inventory.setItem(16, EnhanceGUI.createEnhanceButton(0));
        }
    }

    private void startEnhancementAnimation(Player player, Inventory inventory, int itemSlot, int blackStoneSlot) {
        Material[] countdownBlocks = {Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.GREEN_CONCRETE};
    
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int countdown = 5;
    
            @Override
            public void run() {
                if (countdown > 0) {
                    // Change the block and play the sound
                    inventory.setItem(16, createCountdownItem(countdownBlocks[countdown - 1], countdown));
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
    
    private ItemStack createCountdownItem(Material material, int countdown) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.AQUA + "Enhancing in " + ChatColor.BOLD + countdown + " seconds"));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    


    private void showErrorInGUI(Inventory inventory) {
        ItemStack errorBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = errorBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.RED + "You must use the correct Black Stone for the item you want to enhance."));
            errorBlock.setItemMeta(meta);
        }
        inventory.setItem(16, errorBlock);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            inventory.setItem(16, EnhanceGUI.createEnhanceButton(0));
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

        ItemStack item = inventory.getItem(10);
        ItemStack blackStone = inventory.getItem(13);

        if (item != null) {
            player.getInventory().addItem(item);
        }

        if (blackStone != null) {
            player.getInventory().addItem(blackStone);
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
        ItemMeta itemMeta = itemToEnhance.getItemMeta();

        NamespacedKey enhancementLevelKey = new NamespacedKey(plugin, "enhancement_level");
        int currentLevel = itemMeta.getPersistentDataContainer().getOrDefault(enhancementLevelKey, PersistentDataType.INTEGER, 0);
        double successChance = getSuccessChance(currentLevel);

        Random random = new Random();
        boolean success = random.nextDouble() * 100 < successChance;
    
        if (success) {
            int nextLevel = currentLevel + 1;
            String enhanceName;
            ChatColor enhanceColor;
            switch (nextLevel) {
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
                    enhanceName = String.valueOf(nextLevel);
                    enhanceColor = ChatColor.GREEN;
                    break;
            }
            itemMeta.getPersistentDataContainer().set(enhancementLevelKey, PersistentDataType.INTEGER, nextLevel);
            if (nextLevel > 15) {
                itemMeta.displayName(Component.text(enhanceColor + "" + ChatColor.BOLD + "" + enhanceName));
            } else {
                itemMeta.displayName(Component.text(enhanceColor + "+" + enhanceName));
            }
            itemToEnhance.setItemMeta(itemMeta); // Set the new meta before applying enchantments
            applyEnchantments(itemToEnhance, nextLevel);
            if (nextLevel == 20) {
                inventory.setItem(16, EnhanceGUI.createMaxEnhancementAchievedButton());
            } else {
                Bukkit.broadcastMessage(ChatColor.GREEN + "Enhancement Success: " + enhanceColor + "" + ChatColor.BOLD + itemToEnhance.getType().name() + " " + enhanceName);
                updateEnhanceButton(inventory);
            }
            showSuccessBlock(player, inventory);
            spawnSuccessParticles(player);
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Enhancement Failed: " + ChatColor.AQUA + itemToEnhance.getType().name() + ChatColor.GRAY + " +" + currentLevel);
            showFailureBlock(player, inventory);
        }
    
        if (blackStone.getAmount() > 1) {
            blackStone.setAmount(blackStone.getAmount() - 1);
        } else {
            inventory.setItem(blackStoneSlot, null);
        }
    }
    
    private void showSuccessBlock(Player player, Inventory inventory) {
        ItemStack successBlock = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = successBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.GREEN + "Enhancement Success!"));
            successBlock.setItemMeta(meta);
        }
        inventory.setItem(16, successBlock);
    
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateEnhanceButton(inventory);
        }, 3 * 20L);
    }
    
    private void showFailureBlock(Player player, Inventory inventory) {
        ItemStack failureBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = failureBlock.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(ChatColor.RED + "Enhancement Failed"));
            failureBlock.setItemMeta(meta);
        }
        inventory.setItem(16, failureBlock);
    
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
    
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateEnhanceButton(inventory);
        }, 3 * 20L);
    }
    
    private void applyEnchantments(ItemStack item, int enhancementLevel) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection(String.valueOf(enhancementLevel));
        if (config == null) {
            return;
        }
    
        if (isWeapon(item)) {
            List<String> weaponEnchants = config.getStringList("weapon");
            applyEnchantmentList(item, weaponEnchants);
        } else if (isArmor(item)) {
            List<String> armorEnchants = config.getStringList("armor");
            applyEnchantmentList(item, armorEnchants);
        }
    }
    
    private void applyEnchantmentList(ItemStack item, List<String> enchantmentList) {
        for (String enchantmentString : enchantmentList) {
            String[] enchantmentData = enchantmentString.split(":");
            String enchantmentName = enchantmentData[0];
            int enchantmentLevel = Integer.parseInt(enchantmentData[1]);
    
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
            if (enchantment != null) {
                item.addUnsafeEnchantment(enchantment, enchantmentLevel);
            }
        }
    }
    
    

    
    private double getSuccessChance(int currentLevel) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection(String.valueOf(currentLevel));
        if (config == null) {
            return 0;
        }
    
        double successChance = config.getDouble("success_chance");
        if (successChance <= 0) {
            return 0;
        }
    
        return successChance;
    }
    
    private ChatColor convertTextColorToChatColor(TextColor textColor) {
        for (ChatColor chatColor : ChatColor.values()) {
            if (textColor.asHexString().equalsIgnoreCase(chatColor.toString())) {
                return chatColor;
            }
        }
        return ChatColor.WHITE; // Return a default ChatColor if no match is found
    }
    

    private boolean isWeapon(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE") || type.name().endsWith("_BOW") || type.name().endsWith("_CROSSBOW");
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
    
    private boolean isArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET") || type.name().endsWith("_CHESTPLATE") || type.name().endsWith("_LEGGINGS") || type.name().endsWith("_BOOTS");
    }
}