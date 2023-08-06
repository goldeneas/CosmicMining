package io.github.goldeneas.miningrestrictions.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.FeedbackString;
import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerGainExperience implements Listener {
    private static MiningRestrictions plugin;

    private final Database database;
    private final ExperienceHelper experienceHelper;

    public PlayerGainExperience(MiningRestrictions _plugin, Database database, ExperienceHelper experienceHelper) {
        plugin = _plugin;
        this.database = database;
        this.experienceHelper = experienceHelper;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled())
            return;

        Block block = e.getBlock();
        Player player = e.getPlayer();

        int exp = experienceHelper.getExperienceToGiveForBlock(block);
        database.addExperience(player, exp);

        if(experienceHelper.getExperienceToNextLevel(player) > 0) {
            return;
        }

        int currentLevel = experienceHelper.getCurrentLevelForPlayer(player);
        int expToLevelUp = experienceHelper.getRequiredExperienceForLevel(currentLevel);

        database.addLevels(player, 1);
        database.removeExperience(player, expToLevelUp);

        FeedbackString levelUp = new FeedbackString(plugin);
        levelUp.append("level_up").formatDefault(experienceHelper, player);
        player.sendMessage(levelUp.get());
    }

}