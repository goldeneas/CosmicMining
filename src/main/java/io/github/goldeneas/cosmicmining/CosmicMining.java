package io.github.goldeneas.cosmicmining;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.events.PlayerArmorEquip;
import io.github.goldeneas.cosmicmining.events.PlayerBreakBlock;
import io.github.goldeneas.cosmicmining.events.PlayerAddToDatabase;
import io.github.goldeneas.cosmicmining.helpers.ConfigHelper;
import io.github.goldeneas.cosmicmining.utils.DependencyChecker;
import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class CosmicMining extends JavaPlugin {
    private HashMap<String, YamlDocument> configs;

    @Override
    public void onEnable() {
        configs = new HashMap<>();
        createConfig("config.yml");
        createConfig("messages.yml");

        new DependencyChecker();
        Database database = new Database(this);
        ConfigHelper configHelper = new ConfigHelper(this);
        ExperienceHelper experienceHelper = new ExperienceHelper(database, configHelper);

        getServer().getPluginManager().registerEvents(new PlayerAddToDatabase(database), this);
        getServer().getPluginManager().registerEvents(new PlayerArmorEquip(this, experienceHelper), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlock(this, database, experienceHelper), this);

        checkForUpdates();
        enablePluginMetrics();
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

    private void checkForUpdates() {
        YamlDocument config = getConfig("config.yml");

        final int SPIGOT_RESOURCE_ID = 111794;
        final String UPDATE_PERMISSION = config.getString("update-permission");

        new UpdateChecker(this, UpdateCheckSource.SPIGET, String.valueOf(SPIGOT_RESOURCE_ID))
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(true)
                .setNotifyByPermissionOnJoin(UPDATE_PERMISSION)
                .checkNow();
    }

    private void enablePluginMetrics() {
        final int pluginId = 19461;
        new Metrics(this, pluginId);
    }
}
