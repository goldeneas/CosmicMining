package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.Database;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.OptionalInt;

public class ExperienceHelper {
    private static Database database;

    private static LinkedHashMap<String, Integer> requiredLevelForArmor;
    private static LinkedHashMap<String, Integer> requiredLevelForPickaxe;
    private static LinkedHashMap<String, Integer> requiredExperienceForLevel;

    public ExperienceHelper(Database _database, ConfigHelper configHelper) {
        database = _database;

        requiredLevelForArmor = configHelper
                .getSectionWithIntegers(ConfigPaths.REQUIRED_ARMOR_LEVEL_PATH, "config.yml");

        requiredLevelForPickaxe = configHelper
                .getSectionWithIntegers(ConfigPaths.REQUIRED_PICKAXE_LEVEL_PATH, "config.yml");

        requiredExperienceForLevel = configHelper
                .getSectionWithIntegers(ConfigPaths.REQUIRED_EXPERIENCE_FOR_LEVEL_PATH, "config.yml");
    }

    public int getCurrentLevelForPlayer(Player player) {
        return database.getLevel(player).orElse(0);
    }

    public int getNextLevelForPlayer(Player player) {
        return getCurrentLevelForPlayer(player) + 1;
    }

    public int getCurrentExperienceForPlayer(Player player) {
        return database.getExperience(player).orElse(0);
    }

    public int getPlayerMaxLevel() {
        int currentMaxLevel = 0;

        for(String key : requiredExperienceForLevel.keySet()) {
            int currentMilestone = Integer.parseInt(key);

            if(currentMilestone > currentMaxLevel)
                currentMaxLevel = currentMilestone;
        }

        return currentMaxLevel;
    }

    public int getRequiredExperienceForLevel(int playerLevel) {
        int bestUpperMilestoneGuess = getPlayerMaxLevel();

        for(String key : requiredExperienceForLevel.keySet()) {
            int currentMilestone = Integer.parseInt(key);

            if(bestUpperMilestoneGuess == -1) {
                bestUpperMilestoneGuess = currentMilestone;
                continue;
            }

            if(playerLevel < currentMilestone && currentMilestone < bestUpperMilestoneGuess)
                bestUpperMilestoneGuess = currentMilestone;
        }

        String closestMilestone = String.valueOf(bestUpperMilestoneGuess);
        return requiredExperienceForLevel.getOrDefault(closestMilestone, 0);
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

    public boolean isPlayerMaxLevel(Player player) {
        return getPlayerMaxLevel() <= getCurrentLevelForPlayer(player);
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
