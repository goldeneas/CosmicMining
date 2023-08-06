package io.github.goldeneas.miningrestrictions;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FeedbackString {
    private final YamlDocument messages;
    private final StringBuilder stringBuilder;

    public FeedbackString(MiningRestrictions plugin) {
        this.stringBuilder = new StringBuilder();
        this.messages = plugin.getConfig("messages.yml");
    }

    public FeedbackString append(String path) {
        String message = messages.getString(path);
        String translated = ChatColor.translateAlternateColorCodes('&', message);
        stringBuilder.append(translated);
        return this;
    }

    public FeedbackString formatDefault(ExperienceHelper experienceHelper, Player player) {
        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int currentExperience = experienceHelper.getCurrentExperienceForPlayer(player);

        replace("%player_level%", currentLevel);
        replace("%player_experience%", currentExperience);
        return this;
    }

    public FeedbackString replace(String from, int to) {
        return replace(from, String.valueOf(to));
    }

    public FeedbackString replace(String from, String to) {
        int index = stringBuilder.indexOf(from);
        while (index != -1) {
            stringBuilder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = stringBuilder.indexOf(from, index);
        }

        return this;
    }

    public String get() {
        return stringBuilder.toString();
    }

}
