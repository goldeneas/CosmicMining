package io.github.goldeneas.miningrestrictions;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.events.PlayerBreakBlock;
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
        configs = new HashMap<>();
        createConfig("config.yml");
        createConfig("messages.yml");

        Database database = new Database(this);
        ConfigHelper configHelper = new ConfigHelper(this);
        ExperienceHelper experienceHelper = new ExperienceHelper(database, configHelper);

        getServer().getPluginManager().registerEvents(new PlayerCancelMining(this, database, experienceHelper), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlock(this, database, experienceHelper), this);
    }

    public YamlDocument getConfig(String name) {
        return configs.get(name);
    }

    private void createConfig(String name) {
        try {
            YamlDocument document = YamlDocument.create(new File(getDataFolder(), name), getResource(name));
            configs.put(name, document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
