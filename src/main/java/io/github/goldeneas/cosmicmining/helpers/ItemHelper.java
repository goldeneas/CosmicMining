package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class ItemHelper {
    private final NamespacedKey levelsKey;
    private final NamespacedKey experienceKey;

    private final ConfigHelper configHelper;
    private final HashMap<String, String> requiredLevelForArmor;
    private final HashMap<String, String> requiredLevelForPickaxe;
    private final HashMap<String, String> requiredPickaxesForBlocks;

    public ItemHelper(CosmicMining plugin, ConfigHelper configHelper) {
        this.configHelper = configHelper;

        this.levelsKey = new NamespacedKey(plugin, "levels");
        this.experienceKey = new NamespacedKey(plugin, "experience");

        requiredLevelForArmor = configHelper.getAttributeForArmors("required-level");
        requiredLevelForPickaxe = configHelper.getAttributeForPickaxes("required-level");
        requiredPickaxesForBlocks = configHelper.getAttributeForBlocks("required-pickaxe");
    }

    public Material getRequiredPickaxeForBlock(Block block) {
        Material material = block.getType();
        String requiredItemString = requiredPickaxesForBlocks.get(material.toString());
        return Material.valueOf(requiredItemString);
    }

    public boolean canPickaxeBreakBlock(ItemStack item, Block block) {
        Material itemType = item.getType();
        Material requiredItemType = getRequiredPickaxeForBlock(block);

        int itemWeight = getWeightForPickaxe(itemType);
        int requiredWeight = getWeightForPickaxe(requiredItemType);

        return itemWeight >= requiredWeight;
    }

    public boolean isItemPickaxe(ItemStack item) {
        Material itemMaterial = item.getType();
        return getWeightForPickaxe(itemMaterial) != 0;
    }

    // TODO: PDC related functions can be made shorter by making a common function for them
    // for example, we can make a function that return the PDC and also handles if ItemMeta is null
    public int getItemLevel(ItemStack item) {
        int itemLevel = 0;
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if(pdc.has(levelsKey, PersistentDataType.INTEGER))
            itemLevel = pdc.get(levelsKey, PersistentDataType.INTEGER);

        if(itemLevel == 0)
            itemLevel = 1;

        return itemLevel;
    }

    public void addItemLevel(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        int currentLevel = getItemLevel(item);
        setItemLevel(item, currentLevel + level);
    }

    public void setItemLevel(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(levelsKey, PersistentDataType.INTEGER, level);

        item.setItemMeta(meta);
    }

    public int getItemExperience(ItemStack item) {
        int itemExperience = 0;
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if(pdc.has(experienceKey, PersistentDataType.INTEGER))
            itemExperience = pdc.get(experienceKey, PersistentDataType.INTEGER);

        return itemExperience;
    }

    public void setItemExperience(ItemStack item, int experience) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(experienceKey, PersistentDataType.INTEGER, experience);

        item.setItemMeta(meta);
    }

    public void removeItemExperience(ItemStack item, int experience) {
        addItemExperience(item, -experience);
    }

    public void addItemExperience(ItemStack item, int experience){
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        int currentExperience = getItemExperience(item);
        setItemExperience(item, currentExperience + experience);
    }

    public boolean isPickaxeFullOfExperience(ItemStack item) {
        return getItemExperience(item) >= getPickaxeMaxExperience(item);
    }

    public int getPickaxeMaxExperience(ItemStack item) {
        Material material = item.getType();
        String baseMaxExperienceString = configHelper.getAttributeForPickaxe(material, "base-max-experience");
        String levelMultiplierString = configHelper.getAttributeForPickaxe(material, "per-level-multiplier");

        int levelMultiplier = Integer.parseInt(levelMultiplierString);
        int baseMaxExperience = Integer.parseInt(baseMaxExperienceString);

        return getItemMaxExperience(item, baseMaxExperience, levelMultiplier);
    }

    public int getRequiredLevelForPickaxe(ItemStack item) {
        Material m = item.getType();
        String id = m.toString().toUpperCase();

        String s = requiredLevelForPickaxe.getOrDefault(id, "0");
        return Integer.parseInt(s);
    }

    public int getRequiredLevelForArmor(ItemStack item) {
        String materialString = item.getType().toString();
        String armorType = materialString.split("_")[0];
        String armorsOfType = armorType + "_ARMOR";

        String requiredLevelString = requiredLevelForArmor.getOrDefault(armorsOfType, "0");
        return Integer.parseInt(requiredLevelString);
    }

    private int getItemMaxExperience(ItemStack item, int baseMaxExperience, int levelMultiplier) {
        int itemLevel = getItemLevel(item);

        if(itemLevel == 1)
            return baseMaxExperience;
        else
            return levelMultiplier * itemLevel * baseMaxExperience;
    }

    private int getWeightForPickaxe(Material material) {
        String m = material.toString();

        switch (m) {
            case "WOODEN_PICKAXE":
                return 1;
            case "STONE_PICKAXE":
                return 2;
            case "GOLDEN_PICKAXE":
                return 3;
            case "IRON_PICKAXE":
                return 4;
            case "DIAMOND_PICKAXE":
                return 5;
            case "NETHERITE_PICKAXE":
                return 6;
        }

        return 0;
    }

}
