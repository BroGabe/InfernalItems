package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.configuration.ConfigManager;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.FactionsUtil;
import com.brogabe.infernalitems.utils.WorldGuardUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AntiPearlListeners implements Listener {

    private final InfernalItems plugin;

    private final Set<UUID> blockedPearlPlayers = new HashSet<>();

    public AntiPearlListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        if(itemModule.getItemType(event.getItem()) != InfernalType.ANTI_PEARL) return;

        event.setCancelled(true);

        FileConfiguration config = plugin.getAntiPearlCFG().getConfig();

        CooldownModule cooldownModule = plugin.getModuleManager().getCooldownModule();

        Player player = event.getPlayer();

        if(!itemModule.canUseItem(player, config, InfernalType.ANTI_PEARL)) {
            return;
        }

        List<UUID> nearbyPlayers = player.getNearbyEntities(15, 15, 15).stream()
                .filter(this::isPlayer)
                .map(e -> (Player) e)
                .filter(enemy -> FactionsUtil.isEnemy(player, enemy))
                .map(Player::getUniqueId)
                .collect(Collectors.toList());

        if(nearbyPlayers.isEmpty()) {
            player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fThere are no nearby players"));
            return;
        }

        if(player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() -1);
        } else {
            player.setItemInHand(null);
        }

        player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fYou have &e&nblocked&f nearby enemies!"));

        cooldownModule.addCooldown(player, InfernalType.ANTI_PEARL);
        blockedPearlPlayers.addAll(nearbyPlayers);

        Bukkit.getScheduler().runTaskLater(plugin, () -> nearbyPlayers.forEach(blockedPearlPlayers::remove), 20L * 5);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPearl(PlayerTeleportEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if(!blockedPearlPlayers.contains(event.getPlayer().getUniqueId())) return;

        Player player = event.getPlayer();

        player.sendMessage(ColorUtil.color("&4&lPvP&c&lItems &fAn enemy has blocked your pearl!"));
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 6, 6);
    }

    private boolean isPlayer(Entity entity) {
        return (entity instanceof Player);
    }
}
