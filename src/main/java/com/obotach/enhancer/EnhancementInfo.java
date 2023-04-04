package com.obotach.enhancer;

import org.bukkit.ChatColor;

public class EnhancementInfo {
    private final String enhanceName;
    private final ChatColor enhanceColor;

    public EnhancementInfo(String enhanceName, ChatColor enhanceColor) {
        this.enhanceName = enhanceName;
        this.enhanceColor = enhanceColor;
    }

    public String getEnhanceName() {
        return this.enhanceName;
    }

    public ChatColor getEnhanceColor() {
        return this.enhanceColor;
    }
}