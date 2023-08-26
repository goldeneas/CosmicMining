package io.github.goldeneas.cosmicmining.feedback;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.CosmicMining;

import java.util.HashMap;
import java.util.List;

public class FeedbackLore {
    private static HashMap<String, List<String>> cachedLores;

    private List<String> lore;
    private final YamlDocument lores;

    public FeedbackLore(CosmicMining plugin) {
        this.lores = plugin.getConfig("lores.yml");
    }

    public FeedbackLore loadString(String path) {
        if(!cachedLores.containsKey(path))
            cachedLores.put(path, lores.getStringList(path));

        lore = cachedLores.get(path);
        return this;
    }

    public List<String> get() {
        return lore;
    }
}
