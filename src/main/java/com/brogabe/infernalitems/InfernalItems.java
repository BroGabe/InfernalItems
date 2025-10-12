package com.brogabe.infernalitems;

import co.aikar.commands.PaperCommandManager;
import com.brogabe.infernalitems.commands.ItemCmd;
import com.brogabe.infernalitems.configuration.*;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.listeners.*;
import com.brogabe.infernalitems.menus.ItemsMenu;
import com.brogabe.infernalitems.modules.ModuleManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class InfernalItems extends JavaPlugin {

    @Getter
    private ConfigManager configManager;

    @Getter
    private BlinderCFG blinderCFG;

    @Getter
    private BardCFG bardCFG;

    @Getter
    private WebCFG webCFG;

    @Getter
    private AntiPearlCFG antiPearlCFG;

    @Getter
    private SwitcherCFG switcherCFG;

    @Getter
    private PearlCFG pearlCFG;

    @Getter
    private SpeedCFG speedCFG;

    @Getter
    private StrengthCFG strengthCFG;

    @Getter
    private ModuleManager moduleManager;

    @Getter
    private ItemsMenu menu;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        switcherCFG = new SwitcherCFG(this);
        blinderCFG = new BlinderCFG(this);
        bardCFG = new BardCFG(this);
        webCFG = new WebCFG(this);
        pearlCFG = new PearlCFG(this);
        speedCFG = new SpeedCFG(this);
        strengthCFG = new StrengthCFG(this);
        antiPearlCFG = new AntiPearlCFG(this);
        configManager = new ConfigManager(this);

        moduleManager = new ModuleManager(this);

        menu = new ItemsMenu(this);

        registerListeners();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.getCommandCompletions().registerCompletion("types", c -> Arrays.stream(InfernalType.values()).map(Enum::toString).collect(Collectors.toList()));

        manager.registerCommand(new ItemCmd(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SwitcherListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BlinderListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new PearlListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new WebListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BardListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new PotionItemListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiPearlListeners(this), this);
    }
}
