package com.brogabe.infernalitems.configuration;

import com.brogabe.infernalitems.InfernalItems;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BardCFG {

    private final InfernalItems plugin;

    @Getter
    private FileConfiguration config;

    @Getter
    private File file;

    public BardCFG(InfernalItems plugin) {
        this.plugin = plugin;

        initializeConfig();
    }

    private void initializeConfig() {
        file = new File(plugin.getDataFolder(), "bard.yml");

        if(!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("bard.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        saveConfig();
    }
}
