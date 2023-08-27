package io.github.goldeneas.cosmicmining.feedback;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedbackLore {
    private static HashMap<String, List<String>> cachedLores;

    private List<String> lore;
    private final YamlDocument lores;
    private final ExperienceHelper experienceHelper;

    public FeedbackLore(CosmicMining plugin) {
        cachedLores = new HashMap<>();
        this.lores = plugin.getConfig("lores.yml");
        this.experienceHelper = plugin.getExperienceHelper();
    }

    public FeedbackLore loadString(String path) {
        if(!cachedLores.containsKey(path))
            cachedLores.put(path, lores.getStringList(path));

        lore = cachedLores.get(path);
        return this;
    }

    public List<String> getForPlayer(Player player) {
        ArrayList<String> temp = new ArrayList<>();

        for(String line : lore) {
            String formattedLine = Formatter.setPlaceholders(line, player, experienceHelper);
            temp.add(formattedLine);
        }

        return temp;
    }
}
