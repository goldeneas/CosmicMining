package io.github.goldeneas.miningrestrictions.helpers;

import io.github.goldeneas.miningrestrictions.ConfigPaths;
import io.github.goldeneas.miningrestrictions.Database;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.OptionalInt;

public class ExperienceHelper {
    private static Database database;

    private static LinkedHashMap<String, Integer> requiredLevelForArmor;
    private static LinkedHashMap<String, Integer> requiredLevelForPickaxe;
    private static LinkedHashMap<String, Integer> experienceGivenForBlock;
    private static LinkedHashMap<String, Integer> requiredExperienceForLevel;

    public ExperienceHelper(Database _database, ConfigHelper configHelper) {
        database = _database;

        requiredLevelForArmor = configHelper.loadSectionsInt(ConfigPaths.REQUIRED_ARMOR_LEVEL_PATH);
        requiredLevelForPickaxe = configHelper.loadSectionsInt(ConfigPaths.REQUIRED_PICKAXE_LEVEL_PATH);
        experienceGivenForBlock = configHelper.loadSectionsInt(ConfigPaths.BLOCKS_GIVEN_EXPERIENCE_PATH);
        requiredExperienceForLevel = configHelper.loadSectionsInt(ConfigPaths.REQUIRED_EXPERIENCE_FOR_LEVEL_PATH);
    }

    public int getCurrentLevelForPlayer(Player player) {
        return database.getLevel(player).orElse(0);
    }

    public int getNextLevelForPlayer(Player player) {
        return getCurrentLevelForPlayer(player) + 1;
    }

    public int getCurrentExperienceForPlayer(Player player) {
        int currentLevel = getCurrentLevelForPlayer(player);
        int expToNextLevel = getExperienceToNextLevel(player);
        int requiredExp = getRequiredExperienceForLevel(currentLevel);

        return requiredExp - expToNextLevel;
    }

    public int getRequiredExperienceForLevel(int playerLevel) {
        for(String key : requiredExperienceForLevel.keySet()) {
            int currentLevel = Integer.parseInt(key);

            if(playerLevel <= currentLevel)
                return requiredExperienceForLevel.get(key);
        }

        // TODO: log that no upper milestone was found (player is at max level)
        return Integer.MAX_VALUE;
    }

    public int getExperienceToNextLevel(Player player) {
        int nextLevel = getNextLevelForPlayer(player);
        int requiredExp = getRequiredExperienceForLevel(nextLevel);
        OptionalInt currentExp = database.getExperience(player);

        return requiredExp - (currentExp.orElse(0));
    }

    public int getRequiredLevelForPickaxe(ItemStack item) {
        return getRequiredLevel(item, requiredLevelForPickaxe);
    }

    public int getRequiredLevelForArmor(ItemStack item) {
        return getRequiredLevel(item, requiredLevelForArmor);
    }

    public int getExperienceToGiveForBlock(Block block) {
        Material m = block.getType();
        String id = m.toString();

        return experienceGivenForBlock.getOrDefault(id, 0);
    }

    public boolean canUsePickaxe(Player player, ItemStack item) {
        int requiredLevel = getRequiredLevelForPickaxe(item);
        return canUseItem(player, requiredLevel);
    }

    public boolean canUseArmor(Player player, ItemStack item) {
        int requiredLevel = getRequiredLevelForArmor(item);
        return canUseItem(player, requiredLevel);
    }

    private boolean canUseItem(Player player, int requiredLevel) {
        OptionalInt playerLevel = database.getLevel(player);

        return requiredLevel <= playerLevel.orElse(0);
    }

    private int getRequiredLevel(ItemStack item, LinkedHashMap<String, Integer> map) {
        Material m = item.getType();
        String id = m.toString();

        return map.getOrDefault(id, 0);
    }

}
