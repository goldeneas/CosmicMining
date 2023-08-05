package io.github.goldeneas.miningrestrictions.helpers;

import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemHelper {
    private static MiningRestrictions plugin;
    private static HashMap<String, Integer> requiredLevelForItem;

    public ItemHelper(MiningRestrictions _plugin) {
        plugin = _plugin;
        requiredLevelForItem = new HashMap<>();

        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection s =
                config.getConfigurationSection(ConfigUtil.ITEMS_PATH + ConfigUtil.REQUIRED_LEVELS_PATH);

        if(s == null) {
            Bukkit.getLogger().warning("Could not load config in ItemHelper.java!");
            return;
        }

        for(String item : s.getKeys(false)) {
            int requiredLevel = config.getInt(ConfigUtil.ITEMS_PATH + ConfigUtil.REQUIRED_LEVELS_PATH + item);

            requiredLevelForItem.put(item.toUpperCase(), requiredLevel);
        }
    }

    public Integer getRequiredLevel(ItemStack item) {
        Material m = item.getType();
        String id = m.toString();

        return requiredLevelForItem.getOrDefault(id, 0);
    }

}
