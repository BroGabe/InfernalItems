package com.brogabe.infernalitems.listeners;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.configuration.ConfigManager;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.modules.types.WaterModule;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.FactionsUtil;
import com.brogabe.infernalitems.utils.WorldGuardUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlinderListeners implements Listener {

    private final InfernalItems plugin;

    private final Map<UUID, Instant> lastBlinderThrown = new HashMap<>();

    public BlinderListeners(InfernalItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSnowballInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        ItemModule itemModule = plugin.getModuleManager().getItemModule();

        if(itemModule.getItemType(player.getItemInHand()) != InfernalType.BLINDER) {
            return;
        }

        if(!itemModule.canUseItem(player, plugin.getBlinderCFG().getConfig(), InfernalType.BLINDER)) {
            event.setCancelled(true);
            return;
        }

        lastBlinderThrown.put(player.getUniqueId(), Instant.now());
    }

    @EventHandler
    public void onSnowballLaunch(ProjectileLaunchEvent event) {
        if(!(event.getEntity() instanceof Snowball)) return;

        Snowball snowball = (Snowball) event.getEntity();

        if(!(snowball.getShooter() instanceof Player)) return;

        Player shooter = (Player) snowball.getShooter();

        if(!lastBlinderThrown.containsKey(shooter.getUniqueId())) return;

        Instant lastThrown = lastBlinderThrown.get(shooter.getUniqueId());

        if(Duration.between(lastThrown, Instant.now()).getSeconds() > 1) {
            lastBlinderThrown.remove(shooter.getUniqueId());
            return;
        }

        WaterModule waterModule = plugin.getModuleManager().getWaterModule();
        waterModule.addProjectileEntity(snowball);

        snowball.setMetadata("blinder", new FixedMetadataValue(plugin, "useless"));

        lastBlinderThrown.remove(shooter.getUniqueId());
    }

    @EventHandler
    public void onSnowballDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Snowball)) return;

        Player victim = (Player) event.getEntity();
        Snowball snowball = (Snowball) event.getDamager();

        if(!snowball.hasMetadata("blinder")) return;
        if(!(snowball.getShooter() instanceof Player)) return;

        Player shooter = (Player) snowball.getShooter();

        CooldownModule module = plugin.getModuleManager().getCooldownModule();

        ConfigManager configManager = plugin.getConfigManager();

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 3));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 1));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));

        shooter.sendMessage(ColorUtil.color(configManager.getBlindMsg().replace("%player%", victim.getName())));

        module.addCooldown(shooter, InfernalType.BLINDER);
    }
}
