package com.brogabe.infernalitems.configuration;

import com.brogabe.infernalitems.InfernalItems;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final InfernalItems plugin;

    @Getter
    private String blindMsg;

    @Getter
    private String cooldownMsg;

    @Getter
    private int bardCooldown;

    @Getter
    private int antiPearlCooldown;

    @Getter
    private int fauxPearlCooldown;

    @Getter
    private int speedCooldown;

    @Getter
    private int strengthCooldown;

    @Getter
    private int switcherCooldown;

    @Getter
    private int blinderCooldown;

    @Getter
    private int webCooldown;

    public ConfigManager(InfernalItems plugin) {
        this.plugin = plugin;

        cacheValues();
    }

    private void cacheValues() {
        FileConfiguration config = plugin.getConfig();

        blindMsg = config.getString("messages.blinder-msg");
        cooldownMsg = config.getString("messages.cooldown-msg");

        bardCooldown = plugin.getBardCFG().getConfig().getInt("cooldown");
        antiPearlCooldown = plugin.getAntiPearlCFG().getConfig().getInt("cooldown");
        fauxPearlCooldown = plugin.getPearlCFG().getConfig().getInt("cooldown");
        speedCooldown = plugin.getSpeedCFG().getConfig().getInt("cooldown");
        strengthCooldown = plugin.getStrengthCFG().getConfig().getInt("cooldown");
        switcherCooldown = plugin.getSwitcherCFG().getConfig().getInt("cooldown");
        blinderCooldown = plugin.getBlinderCFG().getConfig().getInt("cooldown");
        webCooldown = plugin.getWebCFG().getConfig().getInt("cooldown");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        cacheValues();
    }
}
