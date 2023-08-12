package io.github.goldeneas.cosmicmining.utils;

import org.bukkit.Bukkit;

public class DependencyChecker {
    public static boolean IS_PLACEHOLDERAPI_AVAILABLE;

    public DependencyChecker() {
        IS_PLACEHOLDERAPI_AVAILABLE = check("PlaceholderAPI");
    }

    private boolean check(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

}
