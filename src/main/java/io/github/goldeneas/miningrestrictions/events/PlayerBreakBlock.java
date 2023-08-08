package io.github.goldeneas.miningrestrictions.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.ConfigPaths;
import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.FeedbackString;
import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class PlayerBreakBlock implements Listener {
    private static MiningRestrictions plugin;

    private final Database database;
    private final YamlDocument config;
    private final ExperienceHelper experienceHelper;

    public PlayerBreakBlock(MiningRestrictions _plugin, Database database, ExperienceHelper experienceHelper) {
        plugin = _plugin;
        config = plugin.getConfig("config.yml");

        this.database = database;
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

        if(!canUseHeldItem(player)) {
            FeedbackString levelTooLow = new FeedbackString(plugin);
            levelTooLow.append("pickaxe-level-too-low").formatDefault(experienceHelper, player);
            player.sendMessage(levelTooLow.get());

            e.setCancelled(true);
            return;
        }

        Block block = e.getBlock();
        if(shouldIgnoreBlock(block))
            return;

        e.setCancelled(true);
        giveExperience(player, block);
        regenerateBlock(block);

        if(shouldLevelUp(player))
            levelUp(player);
    }

    private void regenerateBlock(Block block) {
        BlockState state = block.getState();
        Collection<ItemStack> oldDrops = block.getDrops();

        block.setType(Material.COBBLESTONE);
        Location l = block.getLocation();
        Location blockCenter = l.add(0.5, 0.5, 0.5);

        for(ItemStack item : oldDrops)
            block.getWorld().dropItemNaturally(blockCenter, item);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            state.update(true, false);
        }, 20L);
    }

    private void giveExperience(Player player, Block block) {
        int expToGive = experienceHelper.getExperienceToGiveForBlock(block);
        database.addExperience(player, expToGive);
    };

    private void levelUp(Player player) {
        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int expToLevelUp = experienceHelper.getRequiredExperienceForLevel(currentLevel);

        database.addLevels(player, 1);
        database.removeExperience(player, expToLevelUp);

        FeedbackString levelUp = new FeedbackString(plugin);
        levelUp.append("level-up").formatDefault(experienceHelper, player);
        player.sendMessage(levelUp.get());
    }

    private boolean shouldIgnoreBlock(Block block) {
        return experienceHelper.getExperienceToGiveForBlock(block) == 0;
    }

    private boolean shouldLevelUp(Player player) {
        return experienceHelper.getExperienceToNextLevel(player) <= 0;
    }

    private boolean canUseHeldItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        return experienceHelper.canUsePickaxe(player, heldItem);
    }

}