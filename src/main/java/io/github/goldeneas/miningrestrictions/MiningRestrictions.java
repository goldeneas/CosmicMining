package io.github.goldeneas.miningrestrictions;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.events.PlayerGainExperience;
import io.github.goldeneas.miningrestrictions.events.PlayerCancelMining;
import io.github.goldeneas.miningrestrictions.helpers.ConfigHelper;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class MiningRestrictions extends JavaPlugin {
    private HashMap<String, YamlDocument> configs;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configs = new HashMap<>();

        Database database = new Database(this);
        ConfigHelper configHelper = new ConfigHelper(this);
        ExperienceHelper experienceHelper = new ExperienceHelper(this, database, configHelper);

        createConfig("config.yml");
        createConfig("messages.yml");

        getServer().getPluginManager().registerEvents(new PlayerCancelMining(database, experienceHelper), this);
        getServer().getPluginManager().registerEvents(new PlayerGainExperience(database, experienceHelper), this);
    }

    public YamlDocument getConfig(String name) {
        return configs.get(name);
    }

    private void createConfig(String name) {
        try {
            YamlDocument config = YamlDocument.create(new File(getDataFolder(), name), getResource(name));
            configs.put(name, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
