package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.configuration.ConfigManager;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.FactionsUtil;
import com.brogabe.infernalitems.utils.WorldGuardUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PearlListeners implements Listener {

    private final InfernalItems plugin;

    private final Map<UUID, Instant> lastPearlThrown = new HashMap<>();

    public PearlListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        if(itemModule.getItemType(player.getItemInHand()) != InfernalType.FAUX_PEARL) {
            return;
        }

        if(!itemModule.canUseItem(player, plugin.getPearlCFG().getConfig(), InfernalType.FAUX_PEARL)) {
            event.setCancelled(true);
            return;
        }

        if(lastPearlThrown.containsKey(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou already have a &efake &fenderpearl landing."));
            event.setCancelled(true);
            return;
        }

        lastPearlThrown.put(player.getUniqueId(), Instant.now());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player shooter = event.getPlayer();

        if(!lastPearlThrown.containsKey(shooter.getUniqueId())) return;

        Instant lastThrown = lastPearlThrown.get(shooter.getUniqueId());

        if(Duration.between(lastThrown, Instant.now()).getSeconds() > 5) {
            lastPearlThrown.remove(shooter.getUniqueId());
            return;
        }

        CooldownModule module = plugin.getModuleManager().getCooldownModule();
        module.addCooldown(shooter, InfernalType.FAUX_PEARL);

        lastPearlThrown.remove(shooter.getUniqueId());

        shooter.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou have &efaked&f an &denderpearl."));
        shooter.playSound(shooter.getLocation(), Sound.ENDERMAN_TELEPORT, 6, 6);
        event.setCancelled(true);
    }
}
