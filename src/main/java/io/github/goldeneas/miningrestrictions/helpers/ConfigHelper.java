package io.github.goldeneas.miningrestrictions.helpers;

import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigHelper {
    private static MiningRestrictions plugin;

    public ConfigHelper(MiningRestrictions _plugin) {
        plugin = _plugin;
    }

    protected HashMap<String, Integer> loadSectionsInt(String sectionPath) {
        FileConfiguration config = plugin.getConfig();
        HashMap<String, Integer> temp = new HashMap<>();

        ConfigurationSection s =
                config.getConfigurationSection(sectionPath);

        if(s == null) {
            Bukkit.getLogger().severe("Could not load config sections in " + getClass().getName() + " !");
            return null;
        }

        for(String key : s.getKeys(false)) {
            int value = config.getInt(sectionPath + key);

            temp.put(key, value);
        }

        return temp;
    }
}
