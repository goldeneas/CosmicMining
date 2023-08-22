package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.Database;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.OptionalInt;

public class ItemHelper {
    private final NamespacedKey levelsKey;
    private final NamespacedKey experienceKey;

    private final Database database;
    private final ConfigHelper configHelper;
    private final ExperienceHelper experienceHelper;
    private final HashMap<String, String> requiredPickaxesForBlocks;

    public ItemHelper(CosmicMining _plugin, Database database, ConfigHelper configHelper, ExperienceHelper experienceHelper) {
        this.database = database;
        this.configHelper = configHelper;
        this.experienceHelper = experienceHelper;

        this.levelsKey = new NamespacedKey(_plugin, "levels");
        this.experienceKey = new NamespacedKey(_plugin, "experience");

        requiredPickaxesForBlocks = configHelper.getAttributeForBlocks("required-pickaxe");
    }

    public Material getRequiredPickaxeForBlock(Material blockType) {
        String requiredItemString = requiredPickaxesForBlocks.get(blockType.toString());
        return Material.valueOf(requiredItemString);
    }

    public boolean canPickaxeBreakBlock(ItemStack item, Block block) {
        Material itemType = item.getType();
        Material requiredItemType = getRequiredPickaxeForBlock(block.getType());

        int itemWeight = getWeightForItem(itemType);
        int requiredWeight = getWeightForItem(requiredItemType);

        return itemWeight >= requiredWeight;
    }

    public boolean canPlayerUsePickaxe(Player player, ItemStack item) {
        int requiredLevel = experienceHelper.getRequiredLevelForPickaxe(item);
        return canPlayerUseItem(player, requiredLevel);
    }

    public boolean canPlayerUseArmor(Player player, ItemStack item) {
        int requiredLevel = experienceHelper.getRequiredLevelForArmor(item);
        return canPlayerUseItem(player, requiredLevel);
    }

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
    }

    public void addItemExperience(ItemStack item, int experience){
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        int currentExperience = getItemExperience(item);
        setItemExperience(item, currentExperience + experience);
    }

    private int getItemMaxExperience(ItemStack item, int baseMaxExperience, int levelMultiplier) {
        int itemLevel = getItemLevel(item);

        if(itemLevel == 1)
            return baseMaxExperience;
        else
            return levelMultiplier * itemLevel * baseMaxExperience;
    }

    private boolean canPlayerUseItem(Player player, int requiredLevel) {
        OptionalInt playerLevel = database.getLevel(player);

        return requiredLevel <= playerLevel.orElse(0);
    }

    private int getWeightForItem(Material material) {
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
