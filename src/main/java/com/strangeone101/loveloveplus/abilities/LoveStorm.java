package com.strangeone101.loveloveplus.abilities;

import com.projectkorra.projectkorra.GeneralMethods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoveStorm extends LoveAbility {

    private long chargeTime = 3000;
    private double range = 20;
    private boolean charged = false;
    private long cooldown = 7000;
    private int tick;
    private Random rand = new Random();
    private Location origin;
    private List<Entity>[] entities;

    public LoveStorm(Player player) {
        super(player);

        this.entities = new List[(int)(range * 2)];


        if (bPlayer.canBend(this)) {
            start();
        }
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }

        if (!charged) {
            if (System.currentTimeMillis() > getStartTime() + chargeTime) {
                if (!player.isSneaking()) {
                    this.charged = true;
                    bPlayer.addCooldown(this);
                    this.origin = player.getEyeLocation();

                    for (Entity e : GeneralMethods.getEntitiesAroundPoint(player.getEyeLocation(), range)) {
                        if (!(e instanceof LivingEntity)) continue;

                        double distance = Math.sqrt(e.getLocation().distanceSquared(player.getLocation()));
                        int index = Math.min(39, (int) (distance * 2));

                        if (entities[index] == null) entities[index] = new ArrayList<>();

                        entities[index].add(e);
                    }
                }

                if (getCurrentTick() % 2 == 0) {
                    Location loc = GeneralMethods.getMainHandLocation(player);
                    player.getWorld().spawnParticle(Particle.HEART, loc, 1, 0, 0, 0);
                }
            }
            else if (!player.isSneaking()) {
                remove();
            }
            return;
        }

        tick++;

        for (int amount = 0; amount < 2 * (tick * 2); amount++) {
            Vector vec = new Vector(rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, rand.nextFloat() - 0.5).normalize().multiply(0.5 * tick);

            Location loc = origin.clone().add(vec).add(0, -1, 0);
            this.player.getWorld().spawnParticle(Particle.HEART, loc, 1);
            playLoveParticles(loc.add(0, 1, 0), 3, 0.15);
        }

        Vector soundVec = new Vector(rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, rand.nextFloat() - 0.5).normalize().multiply(0.5 * tick);

        Location soundLoc = origin.clone().add(soundVec);
        player.getWorld().playSound(soundLoc, Sound.ENTITY_CHICKEN_EGG, 0.1F, 2);

        //playLoveParticles(origin, (int)Math.pow(tick, 1.5), (0.3 * tick) / 2);

        if (entities[tick - 1] != null) {
            for (Entity entity : entities[tick - 1]) {
                Location location = entity.getLocation().clone();
                if (entity instanceof LivingEntity) location = ((LivingEntity) entity).getEyeLocation();
                entity.getWorld().spawnParticle(Particle.HEART, location, 12, 0.3, 0.3, 0.3);

                if (entity instanceof Animals && ((Animals) entity).isAdult()) {
                    ((Animals) entity).setBreed(true);
                    ((Animals) entity).setBreedCause(player.getUniqueId());
                    ((Animals) entity).setLoveModeTicks(300);
                }
            }

        }

        if (tick >= range * 2) {
            remove();
            return;
        }
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "LoveStorm";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getInstructions() {
        return "Hold sneak until the ability is charged (heart particles will appear), then release sneak";
    }

    @Override
    public String getDescription() {
        return "Create a storm of love that makes all animals in the area breed!";
    }
}
