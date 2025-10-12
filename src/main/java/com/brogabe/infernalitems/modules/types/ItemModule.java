package com.brogabe.infernalitems.modules.types;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.configuration.ConfigManager;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.items.*;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.FactionsUtil;
import com.brogabe.infernalitems.utils.WorldGuardUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemModule {

    private final InfernalItems plugin;

    public ItemModule(InfernalItems plugin) {
        this.plugin = plugin;
    }

    public void giveItem(CommandSender sender, Player player, String type, int amount) {
        InfernalType infernalType;

        try {
            infernalType = InfernalType.valueOf(type);
        } catch (Exception exception) {
            sender.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fInvalid Item Type!"));
            return;
        }

        sender.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou have given &e" + player.getName() + "&f an item."));

        switch (infernalType) {
            case SWITCHER:
                SwitcherBall.giveItem(player, plugin, amount);
                return;
            case BLINDER:
                BlinderBall.giveItem(player, plugin, amount);
                return;
            case WEB:
                WebItem.giveItem(player, plugin, amount);
                return;
            case FAUX_PEARL:
                PearlItem.giveItem(player, plugin, amount);
                return;
            case ANTI_BARD:
                BardItem.giveItem(player, plugin, amount);
                return;
            case ANTI_PEARL:
                AntiPearlItem.giveItem(player, plugin, amount);
                return;
            case SPEED:
                SpeedItem.giveItem(player, plugin, amount);
                return;
            case STRENGTH:
                StrengthItem.giveItem(player, plugin, amount);
        }
    }

    public boolean canUseItem(Player player, FileConfiguration config, InfernalType type) {
        ConfigManager configManager = plugin.getConfigManager();
        CooldownModule module = plugin.getModuleManager().getCooldownModule();

        if(module.hasCooldown(player, type)) {
            player.sendMessage(ColorUtil.color(configManager.getCooldownMsg()
                    .replace("%seconds%", String.valueOf(module.getCooldownSeconds(player.getPlayer(), type)))));
            return false;
        }

        if(config.getBoolean("deny-at-base") && FactionsUtil.isInBaseRegion(player)) {
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou &e&ncannot&f do this here."));
            return false;
        }

        for(String blacklistedRegion : config.getStringList("blacklisted-regions")) {
            if(!WorldGuardUtil.isInBlacklistedRegion(blacklistedRegion, player.getUniqueId())) continue;
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou &e&ncannot&f do this here."));
            return false;
        }

        return true;
    }

    public InfernalType getItemType(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() == Material.AIR) return InfernalType.INVALID;

        NBTItem nbtItem = new NBTItem(itemStack);

        if(nbtItem.getCompound("InfernalItems") == null) return InfernalType.INVALID;

        NBTCompound compound = nbtItem.getCompound("InfernalItems");

        String type = compound.getString("type");

        InfernalType infernalType;

        try {
            infernalType = InfernalType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return InfernalType.INVALID;
        }

        return infernalType;
    }

}
