package io.github.goldeneas.cosmicmining.helpers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ConfigHelper {
    private static CosmicMining plugin;

    private final YamlDocument armorsConfig;
    private final YamlDocument blocksConfig;
    private final YamlDocument pickaxesConfig;

    public ConfigHelper(CosmicMining _plugin) {
        plugin = _plugin;

        this.armorsConfig = plugin.getConfig("config.yml");
        this.blocksConfig = plugin.getConfig("config.yml");
        this.pickaxesConfig = plugin.getConfig("config.yml");
    }

    // TODO: try removing this method, it's only being used once
    public HashMap<String, String> getSectionWithIntegers(String sectionPath, String configName) {
        YamlDocument config = plugin.getConfig(configName);
        HashMap<String, String> temp = new LinkedHashMap<>();

        Section sections =
                config.getSection(sectionPath);

        if(sections == null) {
            Bukkit.getLogger().severe("Could not load config sections in " + getClass().getName() + " !");
            return null;
        }

        for(Object s : sections.getKeys()) {
            String key = s.toString();
            String value = config.getString(sectionPath + "." + key);

            temp.put(key, value);
        }

        return temp;
    }

    public HashMap<String, String> getAttributeForArmors(String attributeName) {

        for(String m : getMaterials(ConfigPaths.ARMORS_PATH, armorsConfig)) {

        }

        return getAttributes(attributeName, ConfigPaths.ARMORS_PATH, armorsConfig);
    }

    public HashMap<String, String> getAttributeForBlocks(String attributeName) {
        return getAttributes(attributeName, ConfigPaths.BLOCKS_PATH, blocksConfig);
    }

    public HashMap<String, String> getAttributeForPickaxes(String attributeName) {
        return getAttributes(attributeName, ConfigPaths.PICKAXES_PATH, pickaxesConfig);
    }

    public String getAttributeForPickaxe(Material material, String attributeName) {
        String materialString = material.toString();
        return pickaxesConfig.getString(ConfigPaths.PICKAXES_PATH + "." + materialString + "." + attributeName);
    }

    public String getAttributeForArmor(Material material, String attributeName) {
        String materialString = material.toString();
        return pickaxesConfig.getString(ConfigPaths.ARMORS_PATH + "." + materialString + "." + attributeName);
    }

    private ArrayList<String> getMaterials(String path, YamlDocument config) {
        ArrayList<String> materials = new ArrayList<>();

        Section section = config.getSection(path);
        for(Object key : section.getKeys()) {
            String material = key.toString();
            materials.add(material);
        }

        return materials;
    }
    
    private HashMap<String, String> getAttributes(String attributeName, String path, YamlDocument config) {
        HashMap<String, String> attributes = new HashMap<>();

        for(String material : getMaterials(path, config)) {
            String attribute = getAttribute(material, attributeName, path, config);

            if(Objects.equals(attribute, null))
                throw new RuntimeException("Could not load attribute " + attributeName + " for " + material);

            attributes.put(material, attribute);
        }

        return attributes;
    }

    private String getAttribute(String material, String attributeName, String path, YamlDocument config) {
        return config.getString(path + "." + material + "." + attributeName);
    }
}
