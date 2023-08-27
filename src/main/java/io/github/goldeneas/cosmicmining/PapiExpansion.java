package io.github.goldeneas.cosmicmining;

import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    private final ExperienceHelper experienceHelper;

    public PapiExpansion(ExperienceHelper experienceHelper) {
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
        String requiredPlaceholder = "%" + params + "%";
        return Formatter.setPlaceholders(requiredPlaceholder, player, experienceHelper);
    }
}
