package io.github.goldeneas.cosmicmining.helpers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class BlockHelper {
    private final ConfigHelper configHelper;

    private final LinkedHashMap<String, String> requiredItemForBlocks;
    private final LinkedHashMap<String, String> experienceGivenForBlocks;
    private final LinkedHashMap<String, String> secondsToRegenerateBlocks;

    public BlockHelper(ConfigHelper configHelper) {
        this.configHelper = configHelper;

        requiredItemForBlocks = loadAttribute("required-item");
        secondsToRegenerateBlocks = loadAttribute("respawn-time");
        experienceGivenForBlocks = loadAttribute("experience-dropped");
    }

    public boolean canItemBreakBlock(ItemStack item, Block block) {
        Material itemType = item.getType();
        Material requiredItemType = getRequiredItemForBlock(block.getType());

        int itemWeight = getWeightForItem(itemType);
        int requiredWeight = getWeightForItem(requiredItemType);

        return itemWeight >= requiredWeight;
    }

    public Material getRequiredItemForBlock(Material blockType) {
        String requiredItemString = requiredItemForBlocks.get(blockType.toString());
        return Material.valueOf(requiredItemString);
    }

    public int getExperienceToGiveForBlock(Material blockType) {
        String expString = experienceGivenForBlocks.getOrDefault(blockType.toString(), "0");
        return Integer.parseInt(expString);
    }

    public int getSecondsToRegenerateBlock(Material blockType) {
        String respawnTimeString = secondsToRegenerateBlocks.getOrDefault(blockType.toString(), "0");
        return Integer.parseInt(respawnTimeString);
    }

    private LinkedHashMap<String, String> loadAttribute(String attribute) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for(String block : configHelper.getBlocks()) {
            String attributeString = configHelper.getAttributeForBlock(block, attribute);

            map.put(block, attributeString);
        }

        return map;
    }

    // TODO: absolutely change this
    // maybe make this configurable from the config file to allow other items
    private int getWeightForItem(Material material) {
        switch (material) {
            case WOODEN_PICKAXE:
                return 1;
            case STONE_PICKAXE:
                return 2;
            case GOLDEN_PICKAXE:
                return 3;
            case IRON_PICKAXE:
                return 4;
            case DIAMOND_PICKAXE:
                return 5;
            case NETHERITE_PICKAXE:
                return 6;
        }

        return 0;
    }

}
