package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.Database;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.OptionalInt;

public class PlayerHelper {
    private final Database database;
    private final ItemHelper itemHelper;

    private static HashMap<String, String> requiredExperienceForLevel;

    public PlayerHelper(Database database, ItemHelper itemHelper, ConfigHelper configHelper) {
        this.database = database;
        this.itemHelper = itemHelper;

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

    public boolean canPlayerUsePickaxe(Player player, ItemStack item) {
        int requiredLevel = itemHelper.getRequiredLevelForPickaxe(item);
        return canPlayerUseItem(player, requiredLevel);
    }

    public boolean canPlayerUseArmor(Player player, ItemStack item) {
        int requiredLevel = itemHelper.getRequiredLevelForArmor(item);
        return canPlayerUseItem(player, requiredLevel);
    }

    public boolean isPlayerMaxLevel(Player player) {
        return getPlayerMaxLevel() <= getCurrentLevelForPlayer(player);
    }

    private boolean canPlayerUseItem(Player player, int requiredLevel) {
        OptionalInt playerLevel = database.getLevel(player);

        return requiredLevel <= playerLevel.orElse(0);
    }
}
