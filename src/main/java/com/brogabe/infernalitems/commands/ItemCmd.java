package com.brogabe.infernalitems.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("infernalitems|pvpitems")
public class ItemCmd extends BaseCommand {

    private final InfernalItems plugin;

    public ItemCmd(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onMenu(Player sender) {
        plugin.getMenu().openMenu(sender);
    }

    @Subcommand("give")
    @CommandPermission("infernalitems.admin")
    @Syntax("<player> <type> <amount>")
    @CommandCompletion("@players @types @range:1-64")
    public void onGive(CommandSender sender, OnlinePlayer onlinePlayer, String type, int amount) {
        ItemModule itemModule = plugin.getModuleManager().getItemModule();
        itemModule.giveItem(sender, onlinePlayer.getPlayer(), type, amount);
    }

    @Subcommand("reload")
    @CommandPermission("infernalitems.admin")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.getSpeedCFG().reloadConfig();
        plugin.getStrengthCFG().reloadConfig();
        plugin.getBardCFG().reloadConfig();
        plugin.getBlinderCFG().reloadConfig();
        plugin.getPearlCFG().reloadConfig();
        plugin.getWebCFG().reloadConfig();
        plugin.getAntiPearlCFG().reloadConfig();
        plugin.getSwitcherCFG().reloadConfig();
        plugin.getMenu().reload();

        sender.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou have &ereloaded&f the plugin."));
    }
}
