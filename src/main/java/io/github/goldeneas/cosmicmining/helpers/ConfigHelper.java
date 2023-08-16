package io.github.goldeneas.cosmicmining.helpers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ConfigHelper {
    private static CosmicMining plugin;

    private final YamlDocument blocksConfig;

    public ConfigHelper(CosmicMining _plugin) {
        plugin = _plugin;
        this.blocksConfig = plugin.getConfig("config.yml");
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
            temp.put(key, value);
        }

        return temp;
    }

    public ArrayList<String> getBlocks() {
        ArrayList<String> blocks = new ArrayList<>();

        Section sections = blocksConfig.getSection(ConfigPaths.BLOCKS_PATH);
        for(Object key : sections.getKeys()) {
            String blockMaterial = key.toString();
            blocks.add(blockMaterial);
        }

        return blocks;
    }

    public LinkedHashMap<String, String> getAttributeForBlocks(String attribute) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for(String block : getBlocks()) {
            String attributeString = getAttributeForBlock(block, attribute);

            if(Objects.equals(attributeString, null))
                Bukkit.getLogger().severe("Could not load attribute " + attribute + " for " + block);

            map.put(block, attributeString);
        }

        System.out.println(map);

        return map;
    }

    public String getAttributeForBlock(String block, String attributeName) {
        return blocksConfig.getString(ConfigPaths.BLOCKS_PATH + "." + block + "." + attributeName);
    }
}
