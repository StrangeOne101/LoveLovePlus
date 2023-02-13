package com.strangeone101.loveloveplus;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Disguises {

    public static Disguises INSTANCE;

    private boolean enabled = false;
    private Map<Disguise, Set<Player>> cache = new HashMap<>();

    public Disguises() {
        INSTANCE = this;

        if (Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            this.enabled = true;
        }
    }

    public void disguisePassive(LivingEntity entity, Player viewer) {
        if (enabled) {
            Disguise disguise = new MobDisguise(DisguiseType.SHEEP);
            disguise.setNotifyBar(DisguiseConfig.NotifyBar.NONE);
            Set<Player> viewers = new HashSet<>();
            viewers.add(viewer);
            if (DisguiseAPI.isDisguised(entity)) {
                disguise = DisguiseAPI.getDisguise(entity);
                viewers = cache.get(disguise);
                viewers.add(viewer);
            }
            cache.put(disguise, viewers);

            DisguiseAPI.disguiseToPlayers(entity, disguise, viewers);
        }
    }

    public void undisguise(LivingEntity entity, Player viewer) {
        if (enabled) {
            System.out.println("Debug101");
            if (DisguiseAPI.isDisguised(entity)) {
                System.out.println("Debug102");
                Disguise disguise = DisguiseAPI.getDisguise(entity);
                DisguiseAPI.undisguiseToAll(entity);
                Set<Player> viewers = cache.get(disguise);
                viewers.remove(viewer);
                if (viewers.size() == 0) {
                    cache.remove(disguise);
                } else {
                    cache.put(disguise, viewers);
                    DisguiseAPI.disguiseToPlayers(entity, disguise, viewers);
                }
            }
        }
    }
}
