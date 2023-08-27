package io.github.goldeneas.cosmicmining.utils;

import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import io.github.goldeneas.cosmicmining.helpers.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Formatter {

    public static String replacePlayerPlaceholders(String message, Player player, ExperienceHelper experienceHelper) {
        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int currentExperience = experienceHelper.getCurrentExperienceForPlayer(player);
        int requiredExperience = experienceHelper.getRequiredExperienceForLevel(currentLevel);

        message = replace(message, "%player_level%", currentLevel);
        message = replace(message, "%player_experience%", currentExperience);

        String requiredExperienceString = experienceHelper.isPlayerMaxLevel(player) ? "∞" : String.valueOf(requiredExperience);
        message = replace(message, "%player_required_experience%", requiredExperienceString);

        return message;
    }

    public static String replacePickaxePlaceholders(String message, ItemStack item, ItemHelper itemHelper) {
        int currentPickaxeExperience = itemHelper.getItemExperience(item);
        int currentPickaxeMaxExperience = itemHelper.getPickaxeMaxExperience(item);

        message = replace(message, "%pickaxe_experience%", currentPickaxeExperience);
        message = replace(message, "%pickaxe_max_experience%", currentPickaxeMaxExperience);

        return message;
    }

    private static String replace(String message, String from, String to) {
        return message.replace(from, to);
    }

    private static String replace(String message, String from, int to) {
        return message.replace(from, String.valueOf(to));
    }

}
