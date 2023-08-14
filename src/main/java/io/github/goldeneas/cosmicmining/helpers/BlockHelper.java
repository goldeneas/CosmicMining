package io.github.goldeneas.cosmicmining.helpers;

import java.util.LinkedHashMap;

public class BlockHelper {

    private LinkedHashMap<String, String> requiredItemForBlocks;
    private LinkedHashMap<String, Integer> experienceGivenForBlocks;
    private LinkedHashMap<String, Integer> secondsToRegenerateBlocks;

    public BlockHelper(ConfigHelper configHelper) {
        requiredItemForBlocks = getRequiredItemForBlocks();
        experienceGivenForBlocks = getExperienceGivenForBlocks();
        secondsToRegenerateBlocks = getSecondsToRegenerateBlocks();
    }


    private LinkedHashMap<String, String> getRequiredItemForBlocks() {

    }

    private LinkedHashMap<String, Integer> getSecondsToRegenerateBlocks() {

    }

    private LinkedHashMap<String, Integer> getExperienceGivenForBlocks() {

    }

}
