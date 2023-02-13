package com.strangeone101.loveloveplus.abilities;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.strangeone101.loveloveplus.Loveloveplus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OverbearingLove extends LoveAbility {

    public static ItemStack BOW = new ItemStack(Material.BOW);

    public static Map<Arrow, OverbearingLove> ARROWS = new HashMap<>();

    static {
        ItemMeta meta = BOW.getItemMeta();
        Damageable damageable = (Damageable) meta;
        damageable.setDamage(380);
        meta.setDisplayName(Loveloveplus.LOVE.getColor().toString() + "Right click to shoot!");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Shoot your overbearing love!"));
        BOW.setItemMeta(meta);
    }

    private ItemStack oldHand;
    private ItemStack oldSlot9;
    private boolean shot = false;
    private Arrow arrow;
    private Location lastLoc;
    private boolean itemsReverted = false;
    private boolean drawing = false;
    private int slot;

    @Attribute(Attribute.COOLDOWN)
    private long cooldown = 6000;
    @Attribute(Attribute.DAMAGE)
    private double damage = 8;
    @Attribute(Attribute.RANGE)
    private double range = 50;


    private long startDuration = 2000;

    public OverbearingLove(Player player) {
        super(player);

        if (CoreAbility.getAbility(player, OverbearingLove.class) == null) {
            BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

            if (bendingPlayer.canBend(this)) {
                prepare();
                start();
            }
        }
    }

    public void prepare() {
        this.oldHand = player.getInventory().getItemInMainHand().clone();
        this.oldSlot9 = player.getInventory().getItem(9).clone();
        this.slot = player.getInventory().getHeldItemSlot();

        this.drawing = false;

        player.getInventory().setItem(EquipmentSlot.HAND, BOW);
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Override
    public void progress() {
        if (!shot) {
            if (!bPlayer.canBend(this) || this.slot != player.getInventory().getHeldItemSlot()) {
                remove();
            } else if (System.currentTimeMillis() > getStartTime() + 1000 && !this.drawing) { //They didn't shoot the arrow
                remove();
            } else if (this.drawing) {
                if (getCurrentTick() % 4 == 0) { //Play heart particles at the end of the bow
                    Location loc = GeneralMethods.getMainHandLocation(player);

                    player.getWorld().spawnParticle(Particle.HEART, loc, 1, 0, 0, 0);
                }
            }
            return;
        }

        if (this.arrow != null) {
            if (this.arrow.isDead() || this.arrow.getWorld() != player.getWorld() || this.arrow.getLocation().distanceSquared(this.player.getLocation()) > range * range) {
                remove();
                return;
            }

            if (this.arrow.isInBlock()) {
                explode();

                for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.arrow.getLocation(), 3, e -> e instanceof LivingEntity && !(e instanceof ArmorStand) && !e.isDead() && (!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.SPECTATOR))) {
                    if (e == player || e == arrow) continue;
                    DamageHandler.damageEntity(e, this.player, Math.min(damage, damage / e.getLocation().distance(this.arrow.getLocation())), this);
                }

                remove();
                return;
            } else {
                boolean b = false;

                for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.arrow.getLocation(), 1, e -> e instanceof LivingEntity && !(e instanceof ArmorStand) && !e.isDead() && (!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.SPECTATOR))) {
                    if (e == player || e == arrow) continue;

                    DamageHandler.damageEntity(e, this.player, damage, this);
                    b = true;
                }

                if (b) {
                    remove();
                    return;
                }
            }

            Vector direction = arrow.getLocation().toVector().subtract(lastLoc.toVector()).normalize();
            double length = arrow.getLocation().distance(lastLoc);
            for (double d = 0.5; d < length; d += 0.5) {
                Location loc = lastLoc.clone().add(direction.clone().multiply(d));
                player.getWorld().spawnParticle(Particle.HEART, loc, 2, 0.2, 0.2, 0.2);
                playLoveParticles(loc, 5, 0.2);
            }

            player.getWorld().spawnParticle(Particle.HEART, this.arrow.getLocation(), 5, 0.2, 0.2, 0.2);
            lastLoc = arrow.getLocation().clone();
        }


    }

    private void revertItems() {
        if (!itemsReverted) {
            player.getInventory().setItem(slot, oldHand);
            player.getInventory().setItem(9, oldSlot9);

            itemsReverted = true;
        }
    }

    public void interact() {
        this.drawing = true;
    }

    public void shoot(Arrow arrow) {
        this.shot = true;
        this.arrow = arrow;
        this.lastLoc = arrow.getLocation().clone();
        ARROWS.put(arrow, this);

        arrow.setCritical(false);
        arrow.setDamage(0);
        arrow.setFireTicks(0);
        arrow.setKnockbackStrength(0);
        arrow.setBounce(false);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setVelocity(arrow.getVelocity().multiply(2));

        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.5F);
        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.5F);

        revertItems();
    }

    public void damage(Entity entity) {
        Location location = entity.getLocation().clone();
        if (entity instanceof LivingEntity) location = ((LivingEntity) entity).getEyeLocation();

        DamageHandler.damageEntity(entity, this.player, damage, this);
        entity.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 1, 2);

        entity.getWorld().spawnParticle(Particle.HEART, location, 12, 0.3, 0.3, 0.3);

        remove();
    }

    private void explode() {
        Random r = new Random();

        for (int expander = 1; expander <= 9; expander++) {
            int finalExpander = expander;
            Runnable showParticles = () -> {
                for (int amount = 0; amount < 5 * (finalExpander * 2); amount++) {
                    Vector vec = new Vector(r.nextFloat() - 0.5, r.nextFloat() - 0.5, r.nextFloat() - 0.5).normalize().multiply(0.3 * finalExpander);

                    Location loc = arrow.getLocation().clone().add(vec).add(0, -1, 0);

                    this.player.getWorld().spawnParticle(Particle.HEART, loc, 1);
                }
                playLoveParticles(arrow.getLocation(), 10 * finalExpander, (0.3 * finalExpander) / 2);
            };

            Bukkit.getScheduler().runTaskLater(Loveloveplus.INSTANCE, showParticles, expander);

        }

        /*for (int i = 0; i < 50; i++) {
            double xx = (r.nextFloat() - 0.5) * power;
            double yy = (r.nextFloat() - 0.5) * power;
            double zz = (r.nextFloat() - 0.5) * power;

            this.player.getWorld().spawnParticle(Particle.HEART, arrow.getLocation().add(0, -1, 0), 1, xx, yy, zz, 0);
        }*/
    }

    @Override
    public void remove() {
        super.remove();

        revertItems();

        if (this.arrow != null) {
            ARROWS.remove(this.arrow);
            this.arrow.remove();
        }

        if (this.shot) {
            bPlayer.addCooldown(this);
        }
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "OverbearingLove";
    }

    @Override
    public Location getLocation() {
        return arrow != null ? arrow.getLocation() : null;
    }

    @Override
    public String getInstructions() {
        return "Left click to obtain a bow, then shoot to release";
    }

    @Override
    public String getDescription() {
        return "Shower the world in your overbearing love! Love so strong that no one can handle it!";
    }
}
