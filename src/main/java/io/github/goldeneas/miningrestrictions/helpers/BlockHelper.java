package io.github.goldeneas.miningrestrictions.helpers;

import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class BlockHelper {
    private static MiningRestrictions plugin;
    private static HashMap<String, Integer> experienceGivenForBlock;

    public BlockHelper(MiningRestrictions _plugin) {
        plugin = _plugin;
        experienceGivenForBlock = new HashMap<>();

        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection s =
                config.getConfigurationSection(ConfigUtil.BLOCKS_PATH + ConfigUtil.EXPERIENCE_GIVEN_PATH);

        if(s == null) {
            Bukkit.getLogger().warning("Could not load config in BlockHelper.java!");
            return;
        }

        for(String block : s.getKeys(false)) {
            int givenExp = config.getInt(ConfigUtil.BLOCKS_PATH + ConfigUtil.EXPERIENCE_GIVEN_PATH + block);

            experienceGivenForBlock.put(block.toUpperCase(), givenExp);
        }
    }

    public int getGivenExperience(Block block) {
        Material m = block.getType();
        String id = m.toString();

        return experienceGivenForBlock.getOrDefault(id, 0);
    }

}
