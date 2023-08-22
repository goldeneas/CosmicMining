package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.Database;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.OptionalInt;

public class ExperienceHelper {
    private static Database database;

    private static HashMap<String, String> requiredLevelForArmor;
    private static HashMap<String, String> requiredLevelForPickaxe;
    private static HashMap<String, String> requiredExperienceForLevel;

    public ExperienceHelper(Database _database, ConfigHelper configHelper) {
        database = _database;

        requiredLevelForArmor = configHelper.getAttributeForArmors("required-level");
        requiredLevelForPickaxe = configHelper.getAttributeForPickaxes("required-level");
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

        String requiredExperience = requiredExperienceForLevel.getOrDefault(closestMilestone, "0");
        return Integer.parseInt(requiredExperience);
    }

    public int getExperienceToNextLevel(Player player) {
        int nextLevel = getNextLevelForPlayer(player);
        int requiredExp = getRequiredExperienceForLevel(nextLevel);
        OptionalInt currentExp = database.getExperience(player);

        return requiredExp - (currentExp.orElse(0));
    }

    public int getRequiredLevelForPickaxe(ItemStack item) {
        return getRequiredLevelForItem(item, requiredLevelForPickaxe);
    }

    public int getRequiredLevelForArmor(ItemStack item) {
        return getRequiredLevelForItem(item, requiredLevelForArmor);
    }

    public boolean isPlayerMaxLevel(Player player) {
        return getPlayerMaxLevel() <= getCurrentLevelForPlayer(player);
    }

    private int getRequiredLevelForItem(ItemStack item, HashMap<String, String> map) {
        Material m = item.getType();
        String id = m.toString();

        String s = map.getOrDefault(id, "0");
        return Integer.parseInt(s);
    }

}
