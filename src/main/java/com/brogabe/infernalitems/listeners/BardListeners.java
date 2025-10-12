package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.configuration.ConfigManager;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.FactionsUtil;
import com.brogabe.infernalitems.utils.WorldGuardUtil;
import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.classes.Bard;
import com.golfing8.kore.feature.ClassesFeature;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.stream.Collectors;

public class BardListeners implements Listener {

    private final InfernalItems plugin;

    public BardListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        if(itemModule.getItemType(event.getItem()) != InfernalType.ANTI_BARD) return;

        CooldownModule cooldownModule = plugin.getModuleManager().getCooldownModule();

        Player player = event.getPlayer();

        if(!itemModule.canUseItem(player, plugin.getBardCFG().getConfig(), InfernalType.ANTI_BARD)) {
            event.setCancelled(true);
            return;
        }

        ClassesFeature classesFeature = FactionsKore.get().getFeature(ClassesFeature.class);

        Bard bard = classesFeature.getBard();

        List<Entity> nearbyPlayers = player.getNearbyEntities(30, 30, 30).stream().filter(this::isPlayer).collect(Collectors.toList());
        List<Player> bardPlayers = nearbyPlayers.stream().map(e -> (Player) e).filter(bard::isInBard).filter(p -> FactionsUtil.isEnemy(player, p)).collect(Collectors.toList());

        if(bardPlayers.isEmpty()) {
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fThere are no &enearby&f bards."));
            event.setCancelled(true);
            return;
        }

        if(player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() -1);
        } else {
            player.setItemInHand(null);
        }

        for(Player bardPlayer : bardPlayers) {
            bard.removeFromBard(bardPlayer);
        }

        cooldownModule.addCooldown(player, InfernalType.ANTI_BARD);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Player bardPlayer : bardPlayers) {
                if(bardPlayer == null || !bardPlayer.isOnline()) continue;
                if(bard.isInBard(bardPlayer)) continue;
                bard.addToBard(bardPlayer);
            }

            bardPlayers.clear();
        }, 20L * 15);

    }

    private boolean isPlayer(Entity entity) {
        return (entity instanceof Player);
    }

}
