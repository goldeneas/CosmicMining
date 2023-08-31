package io.github.goldeneas.cosmicmining.feedback;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.helpers.PlayerHelper;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

// TODO: add action bar messages
public class FeedbackMessage {
    private static HashMap<String, HashMap<String, String>> cachedMessages;

    private final YamlDocument messages;
    private final PlayerHelper playerHelper;

    private HashMap<String, String> messageProperties;

    private String title;
    private String subtitle;
    private Sound soundToPlay;
    private String chatMessage;
    private String actionBarMessage;

    public FeedbackMessage(CosmicMining plugin) {
        cachedMessages = new HashMap<>();
        this.playerHelper = plugin.getPlayerHelper();
        this.messages = plugin.getConfig("messages.yml");

        this.messageProperties = new HashMap<>();
    }

    public FeedbackMessage load(String path) {
        if(cachedMessages.containsKey(path)) {
            this.messageProperties = cachedMessages.get(path);
            return this;
        }

        Section section = messages.getSection(path);
        if(section == null)
            throw new RuntimeException("Could not find messages path: " +  path);

        for(Object o : section.getKeys()) {
            String propertyName = o.toString();
            String propertyValue = messages.getString(path + "." + propertyName);

            messageProperties.put(propertyName, propertyValue);
        }

        title = getProperty("title");
        subtitle = getProperty("subtitle");
        chatMessage = getProperty("chat-message");
        actionBarMessage = getProperty("action-bar-message");

        String soundName = getProperty("sound").toUpperCase();
        soundToPlay = Sound.valueOf(soundName);

        cachedMessages.put(path, messageProperties);
        return this;
    }

    public void sendTo(Player player) {
        chatMessage = Formatter.replacePlayerPlaceholders(chatMessage, player, playerHelper);
        player.sendMessage(chatMessage);

        actionBarMessage = Formatter.replacePlayerPlaceholders(actionBarMessage, player, playerHelper);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chatMessage));

        if(soundToPlay != null) {
            Location l = player.getLocation();
            player.playSound(l, soundToPlay, 1.0f, 1.0f);
        }

        if(!title.isEmpty()) {
            title = Formatter.replacePlayerPlaceholders(title, player, playerHelper);
            subtitle = Formatter.replacePlayerPlaceholders(subtitle, player, playerHelper);
            player.sendTitle(title, subtitle, 10, 70, 20);
        }
    }

    private String getProperty(String propertyName) {
        return messageProperties.get(propertyName);
    }

}
