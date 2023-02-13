package com.strangeone101.loveloveplus.abilities;

import com.projectkorra.projectkorra.GeneralMethods;
import com.strangeone101.loveloveplus.Disguises;
import com.strangeone101.loveloveplus.Loveloveplus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hypnosis extends LoveAbility {

    public static Set<Monster> MONSTERS = new HashSet<>();
    private static Set<Tameable> TAMES = new HashSet<>();

    private Location location, origin;
    private Vector direction;
    private double speed = 0.8;
    private double radius = 0.5;
    private long duration = 8000;
    private long tameDuration = 1000 * 60 * 2;
    private long cooldown = 3000;
    private double range = 12;

    public Hypnosis(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        this.location = player.getEyeLocation().clone();
        this.origin = this.location.clone();
        this.direction = player.getEyeLocation().getDirection().clone().normalize().multiply(speed);

        start();
    }

    @Override
    public void progress() {
        List<Entity> entityList = GeneralMethods.getEntitiesAroundPoint(location, radius, e -> e instanceof LivingEntity && !(e instanceof ArmorStand) && !e.isDead() && (!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.SPECTATOR));
        entityList.remove(player);

        if (entityList.size() > 0) {
            LivingEntity livingEntity = (LivingEntity) entityList.get(0);



            if (livingEntity instanceof Player && ((Player) livingEntity).isOnline()) {
                Disguises.INSTANCE.disguisePassive(player, (Player) livingEntity);

                Runnable particles = () -> {
                    ((Player) livingEntity).spawnParticle(Particle.HEART, player.getEyeLocation(), 1, player.getWidth() / 2, player.getWidth() / 2, player.getWidth() / 2);
                };

                BukkitTask task = Bukkit.getScheduler().runTaskTimer(Loveloveplus.INSTANCE, particles, 1, 4);

                Bukkit.getScheduler().runTaskLater(Loveloveplus.INSTANCE, () -> {
                    Disguises.INSTANCE.undisguise(player, (Player) livingEntity);
                    task.cancel();
                }, duration / 50);

                remove();
                return;
            } else if (livingEntity instanceof Monster) {
                MONSTERS.add((Monster) livingEntity);

                player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 8, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2);

                if (((Monster) livingEntity).getTarget() == player) {
                    ((Monster) livingEntity).setTarget(null);
                }

                Bukkit.getScheduler().runTaskLater(Loveloveplus.INSTANCE, () -> {
                    MONSTERS.remove((Monster) livingEntity);
                }, duration / 50);

                remove();
                return;
            } else if (livingEntity instanceof Animals) {
                if (livingEntity instanceof Tameable) {
                    if (!((Tameable) livingEntity).isTamed()) {
                        ((Tameable) livingEntity).setTamed(true);
                        ((Tameable) livingEntity).setOwner(player);
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 8, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2);
                        TAMES.add((Tameable) livingEntity);

                        Bukkit.getScheduler().runTaskLater(Loveloveplus.INSTANCE, () -> {
                            TAMES.remove((Tameable) livingEntity);
                            ((Tameable) livingEntity).setOwner(null);
                            ((Tameable) livingEntity).setTamed(false);
                        }, tameDuration / 50);

                        remove();
                        return;
                    }
                } else {
                    ((Animals) livingEntity).setTarget(player);

                    BukkitTask task = Bukkit.getScheduler().runTaskTimer(Loveloveplus.INSTANCE, () -> {
                        ((Animals) livingEntity).setTarget(player);
                        player.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 1, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2, livingEntity.getWidth() / 2);
                    }, 1, 4);

                    Bukkit.getScheduler().runTaskLater(Loveloveplus.INSTANCE, task::cancel, duration / 50);

                    remove();
                    return;
                }
            }
        }

        if (location.getBlock().getType().isSolid()) {
            remove();
            return;
        }

        if (location.distanceSquared(origin) > range * range) {
            remove();
            return;
        }

        location.add(direction);

        playLoveParticles(location, 3, radius);
    }

    @Override
    public void remove() {
        super.remove();

        bPlayer.addCooldown(this);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "Hypnosis";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void stop() {
        for (Tameable tamable : TAMES) {
            tamable.setOwner(null);
            tamable.setTamed(false);
        }
    }

    @Override
    public String getInstructions() {
        return "Click to release";
    }

    @Override
    public String getDescription() {
        return "Hypnotise mobs and players! Hostile mobs will leave you alone for a bit, and dogs and cats will temporarily aid you!";
    }
}
