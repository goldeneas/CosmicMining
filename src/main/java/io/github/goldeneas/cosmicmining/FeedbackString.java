package io.github.goldeneas.cosmicmining;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.utils.DependencyChecker;
import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FeedbackString {
    private static HashMap<String, String> cachedStrings;

    private final YamlDocument messages;
    private final StringBuilder stringBuilder;

    private Sound soundToPlay;

    public FeedbackString(CosmicMining plugin) {
        cachedStrings = new HashMap<>();
        this.stringBuilder = new StringBuilder();
        this.messages = plugin.getConfig("messages.yml");
    }

    public FeedbackString playSound(Sound sound) {
        this.soundToPlay = sound;
        return this;
    }

    public FeedbackString loadString(String path) {
        if(!cachedStrings.containsKey(path))
            cachedStrings.put(path, messages.getString(path));

        String message = cachedStrings.get(path);
        String translated = ChatColor.translateAlternateColorCodes('&', message);
        stringBuilder.append(translated);
        return this;
    }

    public FeedbackString append(String s) {
        stringBuilder.append(s);
        return this;
    }

    public String get() {
        return stringBuilder.toString();
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
            index += to.length();
            index = stringBuilder.indexOf(from, index);
        }

        return this;
    }

    public void sendTo(Player player) {
        sendTo(player, ChatMessageType.CHAT);
    }

    public void sendTo(Player player, ChatMessageType type) {
        String message = stringBuilder.toString();

        if(DependencyChecker.IS_PLACEHOLDERAPI_AVAILABLE)
            message = PlaceholderAPI.setPlaceholders(player, message);

        TextComponent component = new TextComponent(message);
        player.spigot().sendMessage(type, component);

        Location l = player.getLocation();
        player.playSound(l, soundToPlay, 1.0f, 1.0f);
    }

}
