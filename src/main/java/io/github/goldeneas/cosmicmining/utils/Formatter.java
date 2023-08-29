package io.github.goldeneas.cosmicmining.utils;

import io.github.goldeneas.cosmicmining.helpers.PlayerHelper;
import io.github.goldeneas.cosmicmining.helpers.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Formatter {

    public static String replacePlayerPlaceholders(String message, Player player, PlayerHelper playerHelper) {
        int currentLevel = playerHelper.getCurrentLevelForPlayer(player);
        int currentExperience = playerHelper.getCurrentExperienceForPlayer(player);
        int requiredExperience = playerHelper.getRequiredExperienceForLevel(currentLevel);

        message = replace(message, "%player_level%", currentLevel);
        message = replace(message, "%player_experience%", currentExperience);
        message = replace(message, "%player_required_experience%", requiredExperience);

        return message;
    }

    public static String replacePickaxePlaceholders(String message, ItemStack item, ItemHelper itemHelper) {
        int currentPickaxeLevel = itemHelper.getItemLevel(item);
        int currentPickaxeExperience = itemHelper.getItemExperience(item);
        int pickaxeMaxExperience = itemHelper.getPickaxeMaxExperience(item);

        message = replace(message, "%pickaxe_level%", currentPickaxeLevel);
        message = replace(message, "%pickaxe_experience%", currentPickaxeExperience);
        message = replace(message, "%pickaxe_max_experience%", pickaxeMaxExperience);
        message = replace(message, "%pickaxe_experience_bars%", getExperienceBars(currentPickaxeExperience, pickaxeMaxExperience));

        return message;
    }

    private static String replace(String message, String from, String to) {
        return message.replace(from, to);
    }

    private static String replace(String message, String from, int to) {
        return message.replace(from, String.valueOf(to));
    }

    private static String getExperienceBars(float currentExperience, float maxExperience) {
        int barsLength = 25;
        String barSymbol = "▍";
        String fullBarColor = "&d";
        String emptyBarColor = "&7";

        int fullBarsLength = (int) (currentExperience / maxExperience * barsLength);
        int emptyBarsLength = barsLength - fullBarsLength;

        return (fullBarColor + barSymbol).repeat(fullBarsLength) + (emptyBarColor + barSymbol).repeat(emptyBarsLength);
    }

}
