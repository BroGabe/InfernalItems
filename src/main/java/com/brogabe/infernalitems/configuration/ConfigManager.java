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

    public ConfigManager(InfernalItems plugin) {
        this.plugin = plugin;

        cacheValues();
    }

    private void cacheValues() {
        FileConfiguration config = plugin.getConfig();

        blindMsg = config.getString("messages.blinder-msg");
        cooldownMsg = config.getString("messages.cooldown-msg");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        cacheValues();
    }
}
