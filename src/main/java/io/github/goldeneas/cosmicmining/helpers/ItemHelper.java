package io.github.goldeneas.cosmicmining.helpers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class ItemHelper {
    private final LinkedHashMap<String, String> requiredItemForBlocks;

    public ItemHelper(ConfigHelper configHelper) {
        requiredItemForBlocks = configHelper.getAttributeForBlocks("required-item");
    }

    public Material getRequiredItemForBlock(Material blockType) {
        String requiredItemString = requiredItemForBlocks.get(blockType.toString());
        return Material.valueOf(requiredItemString);
    }

    public boolean canItemBreakBlock(ItemStack item, Block block) {
        Material itemType = item.getType();
        Material requiredItemType = getRequiredItemForBlock(block.getType());

        int itemWeight = getWeightForItem(itemType);
        int requiredWeight = getWeightForItem(requiredItemType);

        return itemWeight >= requiredWeight;
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
