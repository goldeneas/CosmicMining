package io.github.goldeneas.cosmicmining.feedback;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.helpers.ItemHelper;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedbackLore {
    private static HashMap<String, List<String>> cachedLores;

    private List<String> lore;
    private final YamlDocument lores;
    private final ItemHelper itemHelper;

    public FeedbackLore(CosmicMining plugin) {
        cachedLores = new HashMap<>();
        this.lores = plugin.getConfig("lores.yml");
        this.itemHelper = plugin.getItemHelper();
    }

    public FeedbackLore loadString(String path) {
        if(!cachedLores.containsKey(path))
            cachedLores.put(path, lores.getStringList(path));

        lore = cachedLores.get(path);
        return this;
    }

    public List<String> getForPickaxe(ItemStack item) {
        ArrayList<String> temp = new ArrayList<>();

        for(String line : lore) {
            String modifiedLine = line;
            modifiedLine = Formatter.replacePickaxePlaceholders(modifiedLine, item, itemHelper);
            modifiedLine = ChatColor.translateAlternateColorCodes('&', modifiedLine);
            temp.add(modifiedLine);
        }

        return temp;
    }
}
