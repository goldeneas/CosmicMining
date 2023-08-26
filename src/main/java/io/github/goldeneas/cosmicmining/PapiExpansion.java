package io.github.goldeneas.cosmicmining;

import io.github.goldeneas.cosmicmining.feedback.FeedbackString;
import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    private static CosmicMining plugin;
    private final ExperienceHelper experienceHelper;

    public PapiExpansion(CosmicMining _plugin, ExperienceHelper experienceHelper) {
        plugin = _plugin;
        this.experienceHelper = experienceHelper;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cosmic";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Goldeneas";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return new FeedbackString(plugin)
                .append("%" + params + "%")
                .formatDefault(experienceHelper, player)
                .get();
    }
}
