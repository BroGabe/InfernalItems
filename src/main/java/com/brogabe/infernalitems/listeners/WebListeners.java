package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.modules.types.WaterModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WebListeners implements Listener {

    private final InfernalItems plugin;

    private final Map<UUID, Instant> lastWebThrown = new HashMap<>();

    public WebListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        if(itemModule.getItemType(player.getItemInHand()) != InfernalType.WEB) {
            return;
        }

        FileConfiguration config = plugin.getWebCFG().getConfig();

        if(!itemModule.canUseItem(player, config, InfernalType.WEB)) {
            event.setCancelled(true);
            return;
        }

        if(lastWebThrown.containsKey(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&4&lPvp&c&lItems &fYou already have a &eweb&f landing!"));
            event.setCancelled(true);
            return;
        }

        lastWebThrown.put(player.getUniqueId(), Instant.now());
    }

    @EventHandler
    public void onSnowballLaunch(ProjectileLaunchEvent event) {
        if(!(event.getEntity() instanceof Egg)) return;

        Egg egg = (Egg) event.getEntity();

        if(!(egg.getShooter() instanceof Player)) return;

        Player shooter = (Player) egg.getShooter();

        if(!lastWebThrown.containsKey(shooter.getUniqueId())) return;

        Instant lastThrown = lastWebThrown.get(shooter.getUniqueId());

        if(Duration.between(lastThrown, Instant.now()).getSeconds() > 1) {
            lastWebThrown.remove(shooter.getUniqueId());
            return;
        }

        WaterModule waterModule = plugin.getModuleManager().getWaterModule();
        waterModule.addProjectileEntity(egg);

        egg.setMetadata("web", new FixedMetadataValue(plugin, "useless"));

        lastWebThrown.remove(shooter.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWebLand(ProjectileHitEvent event) {
        if(!event.getEntity().hasMetadata("web")) return;
        if(event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();

        List<Location> websList = get3x3Area(event.getEntity().getLocation()).stream().filter(this::isAir).collect(Collectors.toList());

        if(websList.isEmpty()) return;

        for(Location location : websList) {
            location.getBlock().setType(Material.WEB);
        }

        CooldownModule module = plugin.getModuleManager().getCooldownModule();
        module.addCooldown(shooter, InfernalType.WEB);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Location location : websList) {
                if(location.getBlock().getType() != Material.WEB) continue;
                location.getBlock().setType(Material.AIR);
            }

            websList.clear();
        }, 20L * 5);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Egg)) return;
        if(!(event.getEntity() instanceof Player)) return;

        Egg egg = (Egg) event.getDamager();

        if(!(egg.getShooter() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player shooter = (Player) egg.getShooter();

        if(!egg.hasMetadata("web")) return;

        egg.removeMetadata("web", plugin);

        Location centerLocation = victim.getLocation().getBlock().getLocation();

        List<Location> websList = get3x3Area(centerLocation).stream().filter(this::isAir).collect(Collectors.toList());

        if(websList.isEmpty()) return;

        for(Location location : websList) {
            location.getBlock().setType(Material.WEB);
        }

        CooldownModule module = plugin.getModuleManager().getCooldownModule();
        module.addCooldown(shooter, InfernalType.WEB);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Location location : websList) {
                if(location.getBlock().getType() != Material.WEB) continue;
                location.getBlock().setType(Material.AIR);
            }

            websList.clear();
        }, 20L * 5);
    }

    @EventHandler
    public void onChickenSpawn(CreatureSpawnEvent event) {
        if(event.getEntity().getType() != EntityType.CHICKEN) return;
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.EGG) return;

        event.setCancelled(true);
    }

    private List<Location> get3x3Area(Location center) {
        List<Location> locations = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = center.clone().add(x, 0, z);
                locations.add(loc);
            }
        }

        return locations;
    }

    private boolean isAir(Location location) {
        return location.getBlock().getType() == Material.AIR;
    }
}
