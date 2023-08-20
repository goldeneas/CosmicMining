package io.github.goldeneas.cosmicmining.helpers;

import org.bukkit.Material;

import java.util.LinkedHashMap;

public class BlockHelper { private final LinkedHashMap<String, String> experienceGivenForBlocks;
    private final LinkedHashMap<String, String> secondsToRegenerateBlocks;

    public BlockHelper(ConfigHelper configHelper) {
        secondsToRegenerateBlocks = configHelper.getAttributeForBlocks("respawn-time");
        experienceGivenForBlocks = configHelper.getAttributeForBlocks("experience-dropped");
    }

    public int getExperienceToGiveForBlock(Material blockType) {
        String expString = experienceGivenForBlocks.getOrDefault(blockType.toString(), "0");
        return Integer.parseInt(expString);
    }

    public int getSecondsToRegenerateBlock(Material blockType) {
        String respawnTimeString = secondsToRegenerateBlocks.getOrDefault(blockType.toString(), "0");
        return Integer.parseInt(respawnTimeString);
    }

}
