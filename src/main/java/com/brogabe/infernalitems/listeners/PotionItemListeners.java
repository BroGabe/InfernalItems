package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionItemListeners implements Listener {

    private final InfernalItems plugin;

    public PotionItemListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        InfernalType type = itemModule.getItemType(event.getItem());

        if(type != InfernalType.SPEED && type != InfernalType.STRENGTH) return;

        CooldownModule cooldownModule = plugin.getModuleManager().getCooldownModule();

        FileConfiguration config = (type == InfernalType.SPEED ? plugin.getSpeedCFG().getConfig() : plugin.getStrengthCFG().getConfig());

        Player player = event.getPlayer();

        if(!itemModule.canUseItem(player, config, type)) {
            event.setCancelled(true);
            return;
        }

        int duration = config.getInt("duration");
        int amplifier = Math.max(0, (config.getInt("amplifier") - 1));

        PotionEffectType potionType;

        try {
            potionType = PotionEffectType.getByName(config.getString("potion-effect"));
        } catch (Exception exception) {
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fInvalid &eeffect&f! Have staff fix this."));
            event.setCancelled(true);
            return;
        }

        if(player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() -1);
        } else {
            player.setItemInHand(null);
        }

        cooldownModule.addCooldown(player, type);

        player.addPotionEffect(new PotionEffect(potionType, 20 * duration, amplifier));
        player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou have activated your effects!"));
    }
}
