package com.brogabe.infernalitems.modules.types;

import com.brogabe.infernalitems.enums.InfernalType;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownModule {

    private final Map<UUID, Map<InfernalType, Instant>> cooldownMap = new HashMap<>();

    public boolean hasCooldown(Player player, InfernalType infernalType) {
        if(!cooldownMap.containsKey(player.getUniqueId())) return false;

        Map<InfernalType, Instant> cooldownTypes = cooldownMap.get(player.getUniqueId());

        if(!cooldownTypes.containsKey(infernalType)) return false;

        Instant attachedCooldown = cooldownTypes.get(infernalType);

        if(Duration.between(attachedCooldown, Instant.now()).getSeconds() < 120) return true;

        cooldownTypes.remove(infernalType);

        if(!cooldownTypes.isEmpty()) return false;

        cooldownMap.remove(player.getUniqueId());

        return false;
    }

    public long getCooldownSeconds(Player player, InfernalType infernalType) {
        if(!hasCooldown(player, infernalType)) return 0;

        Instant activationTime = cooldownMap.get(player.getUniqueId()).get(infernalType);

        return (120 - Duration.between(activationTime, Instant.now()).getSeconds());
    }

    public void addCooldown(Player player, InfernalType infernalType) {
        if(cooldownMap.containsKey(player.getUniqueId())) {
            cooldownMap.get(player.getUniqueId()).put(infernalType, Instant.now());
            return;
        }

        Map<InfernalType, Instant> cooldownTypes = new HashMap<>();
        cooldownTypes.put(infernalType, Instant.now());

        cooldownMap.put(player.getUniqueId(), cooldownTypes);
    }
}
