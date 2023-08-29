package io.github.goldeneas.cosmicmining;

import io.github.goldeneas.cosmicmining.helpers.PlayerHelper;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    private final PlayerHelper playerHelper;

    public PapiExpansion(PlayerHelper playerHelper) {
        this.playerHelper = playerHelper;
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
        return Formatter.replacePlayerPlaceholders(requiredPlaceholder, player, playerHelper);
    }
}
