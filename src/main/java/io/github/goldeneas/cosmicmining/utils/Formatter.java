package io.github.goldeneas.cosmicmining.utils;

import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import org.bukkit.entity.Player;

public class Formatter {

    public static String setPlaceholders(String message, Player player, ExperienceHelper experienceHelper) {
        return replaceAll(message, player, experienceHelper);
    }

    private static String replaceAll(String message, Player player, ExperienceHelper experienceHelper) {
        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int currentExperience = experienceHelper.getCurrentExperienceForPlayer(player);
        int requiredExperience = experienceHelper.getRequiredExperienceForLevel(currentLevel);

        message = replace(message, "%player_level%", String.valueOf(currentLevel));
        message = replace(message, "%player_experience%", String.valueOf(currentExperience));

        if(!experienceHelper.isPlayerMaxLevel(player))
            message = replace(message, "%player_required_experience%", requiredExperience);
        else
            message = replace(message, "%player_required_experience%", "∞");

        return message;
    }

    private static String replace(String message, String from, String to) {
        return message.replace(from, to);
    }

    private static String replace(String message, String from, int to) {
        return message.replace(from, String.valueOf(to));
    }

}
