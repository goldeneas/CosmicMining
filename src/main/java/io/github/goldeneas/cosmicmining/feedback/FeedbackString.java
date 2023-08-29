package io.github.goldeneas.cosmicmining.feedback;

import dev.dejvokep.boostedyaml.YamlDocument;
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

public class FeedbackString {
    private static HashMap<String, String> cachedStrings;

    private final YamlDocument messages;
    private final StringBuilder stringBuilder;
    private final PlayerHelper playerHelper;

    private String title;
    private String subtitle;
    private Sound soundToPlay;

    public FeedbackString(CosmicMining plugin) {
        cachedStrings = new HashMap<>();
        this.stringBuilder = new StringBuilder();
        this.playerHelper = plugin.getPlayerHelper();
        this.messages = plugin.getConfig("messages.yml");

        this.title = "";
        this.subtitle = "";
    }

    public FeedbackString playSound(Sound sound) {
        this.soundToPlay = sound;
        return this;
    }

    public FeedbackString loadString(String path) {
        String message = load(path);
        stringBuilder.append(message);
        return this;
    }

    public FeedbackString loadTitle(String path) {
        this.title = load(path);
        return this;
    }

    public FeedbackString loadSubtitle(String path) {
        this.subtitle = load(path);
        return this;
    }

    private String load(String path) {
        if(!cachedStrings.containsKey(path))
            cachedStrings.put(path, messages.getString(path));

        String message = cachedStrings.get(path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public FeedbackString append(String s) {
        stringBuilder.append(s);
        return this;
    }

    public String get() {
        return stringBuilder.toString();
    }

    public FeedbackString setTitle(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;

        return this;
    }

    public void sendTo(Player player) {
        sendTo(player, ChatMessageType.CHAT);
    }

    public void sendTo(Player player, ChatMessageType type) {
        String message = stringBuilder.toString();
        message = Formatter.replacePlayerPlaceholders(message, player, playerHelper);

        TextComponent component = new TextComponent(message);
        player.spigot().sendMessage(type, component);

        if(soundToPlay != null) {
            Location l = player.getLocation();
            player.playSound(l, soundToPlay, 1.0f, 1.0f);
        }

        if(!title.isEmpty())
            player.sendTitle(title, subtitle, 10, 70, 20);
    }

}
