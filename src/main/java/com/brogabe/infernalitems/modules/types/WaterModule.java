package com.brogabe.infernalitems.modules.types;

import com.brogabe.infernalitems.InfernalItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class WaterModule {

    private final InfernalItems plugin;

    public WaterModule(InfernalItems plugin) {
        this.plugin = plugin;
    }

    private BukkitTask updateTask;

    private final Map<Projectile, Vector> projectileMap = new HashMap<>();

    public void addProjectileEntity(Projectile entity) {
        projectileMap.put(entity, entity.getVelocity());

        startTask();
    }

    public void startTask() {
        if(isRunning()) return;

        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(projectileMap.isEmpty()) {
                updateTask.cancel();
                return;
            }

            Iterator<Projectile> iterator = projectileMap.keySet().iterator();

            while (iterator.hasNext()) {
                Projectile entity = iterator.next();

                if(entity.isDead() || entity.isOnGround()) {
                    iterator.remove();
                    continue;
                }

                Vector velocity = projectileMap.get(entity).multiply(.99f);

                velocity.add(new Vector(0, -0.05000000074505806D, 0));

                entity.setVelocity(velocity);
            }
        }, 1, 1);
    }

    private boolean isRunning() {
        if(updateTask == null) return false;

        return (Bukkit.getScheduler().isCurrentlyRunning(updateTask.getTaskId()) || Bukkit.getScheduler().isQueued(updateTask.getTaskId()));
    }
}
