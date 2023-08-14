package io.github.goldeneas.cosmicmining.helpers;

import org.bukkit.Material;

import java.util.LinkedHashMap;

public class BlockHelper {
    private final ConfigHelper configHelper;

    private final LinkedHashMap<String, String> requiredItemForBlocks;
    private final LinkedHashMap<String, Integer> experienceGivenForBlocks;
    private final LinkedHashMap<String, Integer> secondsToRegenerateBlocks;

    public BlockHelper(ConfigHelper configHelper) {
        this.configHelper = configHelper;

        requiredItemForBlocks = getRequiredItemForBlocks();
        experienceGivenForBlocks = getExperienceGivenForBlocks();
        secondsToRegenerateBlocks = getSecondsToRegenerateBlocks();
    }

    public Material getRequiredItemForBlock(Material blockType) {
        String requiredItemString = requiredItemForBlocks.get(blockType.toString());
        return Material.valueOf(requiredItemString);
    }

    public int getExperienceToGiveForBlock(Material blockType) {
        return experienceGivenForBlocks.getOrDefault(blockType.toString(), 0);
    }

    public int getSecondsToRegenerateBlock(Material blockType) {
        return secondsToRegenerateBlocks.getOrDefault(blockType.toString(), 0);
    }

    private LinkedHashMap<String, String> getRequiredItemForBlocks() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for(String block : configHelper.getBlocks()) {
            String requiredItem = configHelper.getAttributeForBlock(block, "required-item");

            map.put(block, requiredItem);
        }

        return map;
    }

    private LinkedHashMap<String, Integer> getExperienceGivenForBlocks() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        for(String block : configHelper.getBlocks()) {
            String experienceGivenString = configHelper.getAttributeForBlock(block, "experience-dropped");
            int experienceGiven = Integer.parseInt(experienceGivenString);

            map.put(block, experienceGiven);
        }

        return map;
    }

    private LinkedHashMap<String, Integer> getSecondsToRegenerateBlocks() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        for(String block : configHelper.getBlocks()) {
            String secondsToRegenerateString = configHelper.getAttributeForBlock(block, "experience-dropped");
            int secondsToRegenerate = Integer.parseInt(secondsToRegenerateString);

            map.put(block, secondsToRegenerate);
        }

        return map;
    }

}
