package com.strangeone101.loveloveplus.abilities;

import com.projectkorra.projectkorra.GeneralMethods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HealingKiss extends LoveAbility {

    public double healthBoost = 4.5;

    public HealingKiss(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        Entity entity = GeneralMethods.getTargetedEntity(player, 3);

        if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) return;

        LivingEntity livingEntity = (LivingEntity) entity;

        if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
            livingEntity.setHealth(Math.min(livingEntity.getMaxHealth(), livingEntity.getHealth() + healthBoost));

            bPlayer.addCooldown(this);
        }

        livingEntity.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), (int) (livingEntity.getWidth() * 5) + 1, livingEntity.getWidth(), livingEntity.getWidth(), livingEntity.getWidth());
        livingEntity.getWorld().playSound(livingEntity.getEyeLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 2);
    }

    @Override
    public void progress() {

    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 800;
    }

    @Override
    public String getName() {
        return "HealingKiss";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Heal everyone with a healing kiss!";
    }

    @Override
    public String getInstructions() {
        return "Sneak to heal mobs or players in front of you";
    }
}
