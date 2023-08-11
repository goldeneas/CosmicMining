package io.github.goldeneas.cosmicmining.helpers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;

public class ConfigHelper {
    private static CosmicMining plugin;

    public ConfigHelper(CosmicMining _plugin) {
        plugin = _plugin;
    }

    protected LinkedHashMap<String, Integer> loadSectionsInt(String sectionPath) {
        YamlDocument config = plugin.getConfig("config.yml");
        LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();

        Section sections =
                config.getSection(sectionPath);

        if(sections == null) {
            Bukkit.getLogger().severe("Could not load config sections in " + getClass().getName() + " !");
            return null;
        }

        for(Object s : sections.getKeys()) {
            String key = s.toString();

            int value = config.getInt(sectionPath + "." + key);
            temp.put(key.toUpperCase(), value);
        }

        return temp;
    }
}