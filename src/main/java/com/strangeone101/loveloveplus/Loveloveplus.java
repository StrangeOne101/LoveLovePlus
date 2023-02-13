package com.strangeone101.loveloveplus;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Loveloveplus extends JavaPlugin {

    public static Element LOVE;
    public static Loveloveplus INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOVE = new Element("Love", Element.ElementType.BENDING);

        new Disguises();

        ConfigManager.languageConfig.get().addDefault("Chat.Colors.Love", "#ff62a7");
        ConfigManager.languageConfig.get().addDefault("Chat.Colors.LoveSub", "#ff55da");
        ConfigManager.languageConfig.get().addDefault("Abilities.Love.OverbearingLove.DeathMessage", "{victim} couldn't handle {attacker}'s {ability}");
        ConfigManager.languageConfig.save();

        CoreAbility.registerPluginAbilities(this, "com.strangeone101.loveloveplus.abilities");

        Bukkit.getPluginManager().registerEvents(new LoveListener(), this);

        getLogger().info("LoveLovePlus loaded and ready! <3");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
