package io.github.goldeneas.cosmicmining.helpers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ConfigHelper {
    private static CosmicMining plugin;

    public ConfigHelper(CosmicMining _plugin) {
        plugin = _plugin;
    }

    public LinkedHashMap<String, Integer> getSectionWithIntegers(String sectionPath, String configName) {
        YamlDocument config = plugin.getConfig(configName);
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

    private ArrayList<String> getBlocks(String path, String configName) {
        YamlDocument config = plugin.getConfig(configName);
        ArrayList<String> blocks = new ArrayList<>();

        Section sections = config.getSection(path);
        for(Object key : sections.getKeys()) {
            String blockMaterial = key.toString();
            blocks.add(blockMaterial);
        }

        return blocks;
    }

    private LinkedHashMap<String, String> getAttributesForBlock(String path, String configName, String material) {
        YamlDocument config = plugin.getConfig(configName);

        Section section = config.getSection(path + "." + material);
        if(section == null)
            throw new RuntimeException("Could not load attributes sections in " + this.getClass().getName() + "!");

        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        for (Object o : section.getKeys()) {
            String attributeName = o.toString();
            String attributeValue = getAttributeForBlock(path, configName, material, attributeName);

            attributes.put(attributeName, attributeValue);
        }

        return attributes;
    }

    private String getAttributeForBlock(String path, String configName, String material, String attributeName) {
        YamlDocument config = plugin.getConfig(configName);
        return config.getString(path + "." + material + "." + attributeName);
    }
}
