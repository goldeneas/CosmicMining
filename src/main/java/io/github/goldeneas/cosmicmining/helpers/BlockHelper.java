package io.github.goldeneas.cosmicmining.helpers;

import java.util.LinkedHashMap;

public class BlockHelper {

    private LinkedHashMap<String, String> requiredItemForBlocks;
    private LinkedHashMap<String, Integer> experienceGivenForBlocks;
    private LinkedHashMap<String, Integer> secondsToRegenerateBlocks;

    public BlockHelper(ConfigHelper configHelper) {
        requiredItemForBlocks = configHelper.getRequiredItemForBlocks();
        experienceGivenForBlocks = configHelper.getExperienceGivenForBlocks();
        secondsToRegenerateBlocks = configHelper.getSecondsToRegenerateBlocks();
    }

}
