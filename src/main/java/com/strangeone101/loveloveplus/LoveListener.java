package com.strangeone101.loveloveplus;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.PlayerSwingEvent;
import com.strangeone101.loveloveplus.abilities.HealingKiss;
import com.strangeone101.loveloveplus.abilities.Hypnosis;
import com.strangeone101.loveloveplus.abilities.LoveStorm;
import com.strangeone101.loveloveplus.abilities.OverbearingLove;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class LoveListener implements Listener {

    @EventHandler
    public void onClick(PlayerSwingEvent event) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());

        if (bPlayer.getBoundAbilityName().equals("Hypnosis")) {
            new Hypnosis(event.getPlayer());
        } else if (bPlayer.getBoundAbilityName().equals("OverbearingLove")) {
            new OverbearingLove(event.getPlayer());
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());

        if (bPlayer.getBoundAbilityName().equals("HealingKiss") && event.isSneaking()) {
            new HealingKiss(event.getPlayer());
        } else if (bPlayer.getBoundAbilityName().equals("LoveStorm") && event.isSneaking()) {
            new LoveStorm(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Monster && Hypnosis.MONSTERS.contains((Monster)event.getEntity())) {
            event.setCancelled(true);
            event.getEntity().getWorld().spawnParticle(Particle.HEART, ((Monster)event.getEntity()).getEyeLocation(), 8, event.getEntity().getWidth() / 2, event.getEntity().getWidth() / 2, event.getEntity().getWidth() / 2);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        //BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player)event.getEntity());

        if (CoreAbility.hasAbility(event.getPlayer(), OverbearingLove.class)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDraw(PlayerInteractEvent e) {
        if(e.getItem() != null && e.getItem().getType() == Material.BOW) {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                OverbearingLove ol = CoreAbility.getAbility(e.getPlayer(), OverbearingLove.class);
                if (ol != null) {
                    ol.interact();
                }
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player && e.getProjectile() instanceof Arrow) {
            OverbearingLove ol = CoreAbility.getAbility((Player) e.getEntity(), OverbearingLove.class);
            if (ol != null) {
                ol.shoot((Arrow) e.getProjectile());
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && OverbearingLove.ARROWS.containsKey((Arrow)event.getEntity())) {
            OverbearingLove ol = OverbearingLove.ARROWS.get((Arrow)event.getEntity());

            event.setCancelled(true);
            ol.damage(event.getHitEntity());
        }
    }

    @EventHandler
    public void onDamageFromArrow(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && OverbearingLove.ARROWS.containsKey((Arrow)event.getDamager())) {
            event.setCancelled(true);
        }
    }
}
