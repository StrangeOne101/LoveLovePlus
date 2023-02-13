package com.strangeone101.loveloveplus.abilities;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.strangeone101.loveloveplus.Loveloveplus;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public abstract class LoveAbility extends ElementalAbility implements AddonAbility {

    public static final Particle.DustOptions LILY = new Particle.DustOptions(Color.fromRGB(0xff5eb5), 2);
    public static final Particle.DustOptions PINK = new Particle.DustOptions(Color.fromRGB(0xff94ad), 2);
    public static final Particle.DustOptions WHITE = new Particle.DustOptions(Color.WHITE, 2);

    public static final Particle.DustOptions[] LOVE_PARTICLES = {LILY, PINK, WHITE};

    public LoveAbility(Player player) {
        super(player);
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public Element getElement() {
        return Loveloveplus.LOVE;
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public String getAuthor() {
        return Loveloveplus.INSTANCE.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return Loveloveplus.INSTANCE.getDescription().getVersion();
    }

    public void playLoveParticles(Location location, int amount, double offset) {
        int special = (int)(getCurrentTick() * 1337);

        for (int i = special; i < special + amount; i++) {
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, offset, offset, offset, LOVE_PARTICLES[i % 3]);
        }
    }
}
