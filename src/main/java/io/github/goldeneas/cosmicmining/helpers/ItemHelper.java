package io.github.goldeneas.cosmicmining.helpers;

import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.feedback.FeedbackLore;
import io.github.goldeneas.cosmicmining.utils.Formatter;
import io.github.goldeneas.cosmicmining.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;

public class ItemHelper {
    private final NamespacedKey levelsKey;
    private final NamespacedKey enchantsKey;
    private final NamespacedKey experienceKey;

    private final CosmicMining plugin;
    private final ConfigHelper configHelper;
    private final HashMap<String, String> requiredLevelForArmor;
    private final HashMap<String, String> requiredLevelForPickaxe;
    private final HashMap<String, String> requiredPickaxesForBlocks;

    public ItemHelper(CosmicMining plugin, ConfigHelper configHelper) {
        this.plugin = plugin;
        this.configHelper = configHelper;

        this.levelsKey = new NamespacedKey(plugin, "levels");
        this.enchantsKey = new NamespacedKey(plugin, "enchants");
        this.experienceKey = new NamespacedKey(plugin, "experience");

        requiredLevelForArmor = configHelper.getAttributeForArmors("required-level");
        requiredLevelForPickaxe = configHelper.getAttributeForPickaxes("required-level");
        requiredPickaxesForBlocks = configHelper.getAttributeForBlocks("required-pickaxe");
    }

    public int[] getItemEnchants(ItemStack item) {
        return getPersistentProperty(item, PersistentDataType.INTEGER_ARRAY, enchantsKey);
    }

    public void addItemEnchants(ItemStack item, int... enchantsToAdd) {
        int[] oldEnchants = getItemEnchants(item);
        int[] finalEnchants = new int[oldEnchants.length + enchantsToAdd.length];

        System.arraycopy(oldEnchants, 0, finalEnchants, 0, oldEnchants.length);
        System.arraycopy(enchantsToAdd, 0, finalEnchants, oldEnchants.length, enchantsToAdd.length);

        setItemEnchants(item, finalEnchants);
    }

    public void removeItemEnchants(ItemStack item, int... enchantsToRemove) {
        int[] oldEnchants = getItemEnchants(item);
        int[] finalEnchants = new int[oldEnchants.length - enchantsToRemove.length];

        int freeIndex = 0;
        for(int oldEnchant : oldEnchants) {
            for(int enchantToRemove : enchantsToRemove) {
                if (oldEnchant == enchantToRemove)
                    continue;

                finalEnchants[freeIndex] = oldEnchant;
                ++freeIndex;
            }
        }

        setItemEnchants(item, finalEnchants);
    }

    public void setItemEnchants(ItemStack item, int... enchantIds) {
        setPersistentProperty(item, enchantIds, PersistentDataType.INTEGER_ARRAY, enchantsKey);
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

    public int getItemLevel(ItemStack item) {
        int itemLevel = getPersistentProperty(item, PersistentDataType.INTEGER, levelsKey);

        if(itemLevel == 0)
            itemLevel = 1;

        return itemLevel;
    }

    public void addItemLevel(ItemStack item, int level) {
        int currentLevel = getItemLevel(item);
        setItemLevel(item, currentLevel + level);
    }

    public void setItemLevel(ItemStack item, int level) {
        setPersistentProperty(item, level, PersistentDataType.INTEGER, levelsKey);
    }

    public int getItemExperience(ItemStack item) {
        return getPersistentProperty(item, PersistentDataType.INTEGER, experienceKey);
    }

    public void removeItemExperience(ItemStack item, int experience) {
        addItemExperience(item, -experience);
    }

    public void addItemExperience(ItemStack item, int experience){
        int currentExperience = getItemExperience(item);
        setItemExperience(item, currentExperience + experience);
    }

    public void setItemExperience(ItemStack item, int experience) {
        setPersistentProperty(item, experience, PersistentDataType.INTEGER, experienceKey);
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

    public void refreshPickaxeMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        List<String> lore = new FeedbackLore(plugin)
                .load("pickaxe-lore")
                .getForPickaxe(item);

        String baseName = StringUtils.fixMaterialName(item.getType());
        String formattedName = baseName + "&7[%pickaxe_level%]";

        formattedName = Formatter.replacePickaxePlaceholders(formattedName, item, this);

        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.setDisplayName(formattedName);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
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

    private PersistentDataContainer getPDC(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            throw new UnsupportedOperationException("Could not get meta for " + item.getType());

        return meta.getPersistentDataContainer();
    }

    private <T, Z> boolean hasPersistentProperty(ItemStack item, PersistentDataType<T, Z> type, NamespacedKey key) {
        PersistentDataContainer pdc = getPDC(item);
        return pdc.has(key, type);
    }

    private <T, Z> Z getPersistentProperty(ItemStack item, PersistentDataType<T, Z> type, NamespacedKey key) {
        PersistentDataContainer pdc = getPDC(item);
        if(!pdc.has(key, type))
            throw new UnsupportedOperationException("Could not find property " + key.getKey() + " in " + item.getType());

        return pdc.get(key, type);
    }

    private <T, Z> void setPersistentProperty(ItemStack item, Z value, PersistentDataType<T, Z> type, NamespacedKey key) {
        PersistentDataContainer pdc = getPDC(item);
        pdc.set(key, type, value);
    }

}
