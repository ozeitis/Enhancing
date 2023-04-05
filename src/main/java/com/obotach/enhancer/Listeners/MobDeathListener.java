package com.obotach.enhancer.Listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.obotach.enhancer.CustomItems;
import com.obotach.enhancer.Enhancing;

import java.util.Random;

public class MobDeathListener implements Listener {
    private final Random random = new Random();
    private double blackStoneDropChance;
    private double concentratedBlackStoneDropChance;

    public MobDeathListener(double blackStoneDropChance, double concentratedBlackStoneDropChance) {
        this.blackStoneDropChance = blackStoneDropChance;
        this.concentratedBlackStoneDropChance = concentratedBlackStoneDropChance;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (isHostileMob(entity) && shouldDropBlackStone()) {
            ItemStack blackStone = random.nextBoolean() ? CustomItems.createBlackStoneWeapon() : CustomItems.createBlackStoneArmor();
            entity.getWorld().dropItemNaturally(entity.getLocation(), blackStone);
        }
        if (isHostileMob(entity) && shouldDropConcentratedBlackStone()) {
            ItemStack blackStone = random.nextBoolean() ? CustomItems.createConcentratedMagicalBlackStoneWeapon() : CustomItems.createConcentratedMagicalBlackStoneArmor();
            entity.getWorld().dropItemNaturally(entity.getLocation(), blackStone);
        }
    }

    private boolean isHostileMob(LivingEntity entity) {
        EntityType entityType = entity.getType();
        return entityType == EntityType.ZOMBIE || entityType == EntityType.SKELETON || entityType == EntityType.CREEPER
                || entityType == EntityType.ENDERMAN || entityType == EntityType.BLAZE || entityType == EntityType.WITHER_SKELETON
                || entityType == EntityType.GHAST || entityType == EntityType.MAGMA_CUBE || entityType == EntityType.SLIME
                || entityType == EntityType.PHANTOM || entityType == EntityType.DROWNED || entityType == EntityType.HUSK
                || entityType == EntityType.STRAY || entityType == EntityType.PILLAGER || entityType == EntityType.RAVAGER
                || entityType == EntityType.SHULKER || entityType == EntityType.SILVERFISH || entityType == EntityType.SPIDER
                || entityType == EntityType.CAVE_SPIDER || entityType == EntityType.VEX || entityType == EntityType.VINDICATOR
                || entityType == EntityType.EVOKER || entityType == EntityType.WITCH || entityType == EntityType.WITHER
                || entityType == EntityType.ELDER_GUARDIAN || entityType == EntityType.GUARDIAN || entityType == EntityType.HOGLIN
                || entityType == EntityType.PIGLIN || entityType == EntityType.PIGLIN_BRUTE || entityType == EntityType.STRIDER
                || entityType == EntityType.ZOGLIN || entityType == EntityType.ZOMBIFIED_PIGLIN;
    }

    private boolean shouldDropBlackStone() {
        return random.nextInt(100) < blackStoneDropChance;
    }

    private boolean shouldDropConcentratedBlackStone() {
        return random.nextInt(100) < concentratedBlackStoneDropChance;
    }
}
