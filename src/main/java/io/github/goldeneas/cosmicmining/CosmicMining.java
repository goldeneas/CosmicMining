package io.github.goldeneas.cosmicmining;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.events.PlayerArmorEquip;
import io.github.goldeneas.cosmicmining.events.PlayerBreakBlock;
import io.github.goldeneas.cosmicmining.events.PlayerAddToDatabase;
import io.github.goldeneas.cosmicmining.helpers.*;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.utils.DependencyChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class CosmicMining extends JavaPlugin {
    private HashMap<String, YamlDocument> configs;

    private Database database;
    private ItemHelper itemHelper;
    private BlockHelper blockHelper;
    private ConfigHelper configHelper;
    private PlayerHelper playerHelper;

    @Override
    public void onEnable() {
        configs = new HashMap<>();
        createConfig("lores.yml");
        createConfig("config.yml");
        createConfig("messages.yml");
        createConfig("enchants.yml");

        database = new Database(this);
        configHelper = new ConfigHelper(this);
        blockHelper = new BlockHelper(configHelper);
        itemHelper = new ItemHelper(this, configHelper);
        playerHelper = new PlayerHelper(database, itemHelper, configHelper);

        checkForDependencies();
        if(DependencyChecker.IS_PLACEHOLDERAPI_AVAILABLE)
            new PapiExpansion(playerHelper).register();

        getServer().getPluginManager().registerEvents(new PlayerBreakBlock(this), this);
        getServer().getPluginManager().registerEvents(new PlayerArmorEquip(this), this);
        getServer().getPluginManager().registerEvents(new PlayerAddToDatabase(this), this);

        checkForUpdates();
        enablePluginMetrics();
    }

    public YamlDocument getConfig(String name) {
        return configs.get(name);
    }

    public ItemHelper getItemHelper() {
        return itemHelper;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public PlayerHelper getPlayerHelper() {
        return playerHelper;
    }

    public BlockHelper getBlockHelper() {
        return blockHelper;
    }

    public Database getDatabase() {
        return database;
    }

    private void createConfig(String name) {
        try {
            YamlDocument document = YamlDocument.create(new File(getDataFolder(), name), getResource(name));
            configs.put(name, document);
        } catch (IOException e) {
            throw new RuntimeException("Could not create config file " + name + " in plugin's folder!");
        }
    }

    private void checkForUpdates() {
        YamlDocument config = getConfig("config.yml");

        final int SPIGOT_RESOURCE_ID = 111794;
        final String UPDATE_PERMISSION = config.getString(ConfigPaths.UPDATE_PERMISSION_PATH);

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

    private void checkForDependencies() {
        new DependencyChecker();
    }
}
