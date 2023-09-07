package io.github.goldeneas.cosmicmining.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.animations.ChargedPummel;
import io.github.goldeneas.cosmicmining.feedback.FeedbackLore;
import io.github.goldeneas.cosmicmining.helpers.BlockHelper;
import io.github.goldeneas.cosmicmining.helpers.ItemHelper;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import io.github.goldeneas.cosmicmining.Database;
import io.github.goldeneas.cosmicmining.feedback.FeedbackMessage;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.helpers.PlayerHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Collection;

public class PlayerBreakBlock implements Listener {
    private static CosmicMining plugin;

    private final Database database;
    private final YamlDocument config;
    private final ItemHelper itemHelper;
    private final BlockHelper blockHelper;
    private final PlayerHelper playerHelper;

    public PlayerBreakBlock(CosmicMining _plugin) {
        plugin = _plugin;
        config = plugin.getConfig("config.yml");

        this.database = plugin.getDatabase();
        this.itemHelper = plugin.getItemHelper();
        this.blockHelper = plugin.getBlockHelper();
        this.playerHelper = plugin.getPlayerHelper();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled())
            return;

        Player player = e.getPlayer();
        new ChargedPummel(player, plugin)
                .play();

        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        Block block = e.getBlock();
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        if(shouldIgnoreBlock(block) || shouldIgnoreItem(heldItem))
            return;

        e.setCancelled(true);
        if(!playerHelper.canPlayerUsePickaxe(player, heldItem)) {
            denyPickaxeUsageFeedback(player);
            return;
        }

        if(!itemHelper.canPickaxeBreakBlock(heldItem, block)) {
            preventBlockBreakFeedback(player);
            return;
        }

        if(!playerHelper.isPlayerMaxLevel(player))
            giveExperienceToPlayer(player, block);

        giveExperienceToPickaxe(heldItem, block);
        if(itemHelper.isPickaxeFullOfExperience(heldItem))
            pickaxeLevelUp(player, heldItem);

        itemHelper.refreshPickaxeMeta(heldItem);
        giveBlockDrops(player, block);
        regenerateBlock(block);

        // DEBUG
        System.out.println("Current enchants: " + Arrays.toString(itemHelper.getItemEnchants(heldItem)));
        System.out.println("Adding enchant with ID 0");
        itemHelper.addItemEnchants(heldItem, 0);
        System.out.println("Current enchants: " + Arrays.toString(itemHelper.getItemEnchants(heldItem)));
        System.out.println("Adding enchant with ID 0 1 2 3");
        itemHelper.addItemEnchants(heldItem, 0, 1, 2, 3);
        System.out.println("Current enchants: " + Arrays.toString(itemHelper.getItemEnchants(heldItem)));
        System.out.println("Removing enchants 0 1 4");
        itemHelper.removeItemEnchants(heldItem, 0, 1, 4);
        System.out.println("Current enchants: " + Arrays.toString(itemHelper.getItemEnchants(heldItem)));



        if(shouldPlayerLevelUp(player))
            playerLevelUp(player);
    }

    private void giveBlockDrops(Player player, Block block) {
        PlayerInventory inventory = player.getInventory();
        Collection<ItemStack> oldDrops = block.getDrops();

        for(ItemStack item : oldDrops) {
            var itemsNotStored = inventory.addItem(item);
            if(itemsNotStored.isEmpty())
                continue;

            new FeedbackMessage(plugin)
                    .load("inventory-full")
                    .sendTo(player);
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

    private void pickaxeLevelUp(Player player, ItemStack item) {
        int pickaxeMaxExperience = itemHelper.getPickaxeMaxExperience(item);

        itemHelper.addItemLevel(item, 1);
        itemHelper.removeItemExperience(item, pickaxeMaxExperience);

        new FeedbackMessage(plugin)
                .load("pickaxe-level-up")
                .sendTo(player);
    }

    private void playerLevelUp(Player player) {
        int currentLevel = playerHelper.getCurrentLevelForPlayer(player);
        int expToLevelUp = playerHelper.getRequiredExperienceForLevel(currentLevel);

        database.addLevels(player, 1);
        database.removeExperience(player, expToLevelUp);

        new FeedbackMessage(plugin)
                .load("level-up")
                .sendTo(player);
    }

    private void denyPickaxeUsageFeedback(Player player) {
        new FeedbackMessage(plugin)
                .load("pickaxe-level-too-low")
                .sendTo(player);
    }

    private boolean shouldIgnoreBlock(Block block) {
        Material blockType = block.getType();
        return blockHelper.getExperienceToGiveForBlock(blockType) == 0;
    }

    private boolean shouldIgnoreItem(ItemStack item) {
        return !itemHelper.isItemPickaxe(item);
    }

    private boolean shouldPlayerLevelUp(Player player) {
        return playerHelper.getExperienceToNextLevel(player) <= 0;
    }

    private void preventBlockBreakFeedback(Player player) {
        new FeedbackMessage(plugin)
                .load("incorrect-item")
                .sendTo(player);
    }

    private void giveExperienceToPickaxe(ItemStack item, Block block) {
        Material blockType = block.getType();
        int exp = blockHelper.getExperienceToGiveForBlock(blockType);
        itemHelper.addItemExperience(item, exp);
    }

}