package io.github.goldeneas.cosmicmining.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.feedback.FeedbackLore;
import io.github.goldeneas.cosmicmining.helpers.BlockHelper;
import io.github.goldeneas.cosmicmining.helpers.ItemHelper;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.Database;
import io.github.goldeneas.cosmicmining.feedback.FeedbackString;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.helpers.ExperienceHelper;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerBreakBlock implements Listener {
    private static CosmicMining plugin;

    private final Database database;
    private final YamlDocument config;
    private final ItemHelper itemHelper;
    private final BlockHelper blockHelper;
    private final ExperienceHelper experienceHelper;

    public PlayerBreakBlock(CosmicMining _plugin, Database database, BlockHelper blockHelper,
                            ExperienceHelper experienceHelper, ItemHelper itemHelper) {
        plugin = _plugin;
        config = plugin.getConfig("config.yml");

        this.database = database;
        this.itemHelper = itemHelper;
        this.blockHelper = blockHelper;
        this.experienceHelper = experienceHelper;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled())
            return;

        Player player = e.getPlayer();
        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        Block block = e.getBlock();
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        if(shouldIgnoreBlock(block) || shouldIgnoreItem(heldItem))
            return;

        e.setCancelled(true);
        if(!itemHelper.canPlayerUsePickaxe(player, heldItem)) {
            denyPickaxeUsageFeedback(player);
            return;
        }

        if(!itemHelper.canPickaxeBreakBlock(heldItem, block)) {
            preventBlockBreakFeedback(player);
            return;
        }

        if(!experienceHelper.isPlayerMaxLevel(player))
            giveExperienceToPlayer(player, block);

        if(!itemHelper.isPickaxeFullOfExperience(heldItem)) {
            giveExperienceToPickaxe(heldItem, block);
            refreshPickaxeLore(heldItem);
        }

        giveBlockDrops(player, block);
        regenerateBlock(block);

        if(shouldLevelUp(player))
            levelUp(player);
    }

    private void giveBlockDrops(Player player, Block block) {
        PlayerInventory inventory = player.getInventory();
        Collection<ItemStack> oldDrops = block.getDrops();

        for(ItemStack item : oldDrops) {
            var itemsNotStored = inventory.addItem(item);
            if(itemsNotStored.isEmpty())
                continue;

            new FeedbackString(plugin)
                    .loadString("inventory-full")
                    .formatDefault(experienceHelper, player)
                    .playSound(Sound.ENTITY_PAINTING_BREAK)
                    .sendTo(player, ChatMessageType.ACTION_BAR);
        }
    }

    private void regenerateBlock(Block block) {
        BlockState state = block.getState();
        Material blockType = block.getType();

        block.setType(Material.COBBLESTONE);

        long secondsToRegenerate = blockHelper.getSecondsToRegenerateBlock(blockType);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                state.update(true, false), secondsToRegenerate * 20);
    }

    private void giveExperienceToPlayer(Player player, Block block) {
        Material blockType = block.getType();
        int expToGive = blockHelper.getExperienceToGiveForBlock(blockType);
        database.addExperience(player, expToGive);
    }

    private void levelUp(Player player) {
        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int expToLevelUp = experienceHelper.getRequiredExperienceForLevel(currentLevel);

        database.addLevels(player, 1);
        database.removeExperience(player, expToLevelUp);

        new FeedbackString(plugin)
                .loadString("level-up")
                .formatDefault(experienceHelper, player)
                .playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                .loadTitle("level-up-title")
                .sendTo(player);
    }

    private void denyPickaxeUsageFeedback(Player player) {
        new FeedbackString(plugin)
                .loadString("pickaxe-level-too-low")
                .formatDefault(experienceHelper, player)
                .playSound(Sound.ENTITY_VILLAGER_NO)
                .sendTo(player, ChatMessageType.ACTION_BAR);
    }

    private boolean shouldIgnoreBlock(Block block) {
        Material blockType = block.getType();
        return blockHelper.getExperienceToGiveForBlock(blockType) == 0;
    }

    private boolean shouldIgnoreItem(ItemStack item) {
        return !itemHelper.isItemPickaxe(item);
    }

    private boolean shouldLevelUp(Player player) {
        return experienceHelper.getExperienceToNextLevel(player) <= 0;
    }

    private void preventBlockBreakFeedback(Player player) {
        new FeedbackString(plugin)
                .loadString("incorrect-item")
                .formatDefault(experienceHelper, player)
                .playSound(Sound.ENTITY_VILLAGER_NO)
                .sendTo(player, ChatMessageType.ACTION_BAR);
    }

    private void giveExperienceToPickaxe(ItemStack item, Block block) {
        Material blockType = block.getType();
        int exp = blockHelper.getExperienceToGiveForBlock(blockType);
        itemHelper.addItemExperience(item, exp);
    }

    private void refreshPickaxeLore(ItemStack item) {
        List<String> lore = new FeedbackLore(plugin)
                .get();

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
    }

}