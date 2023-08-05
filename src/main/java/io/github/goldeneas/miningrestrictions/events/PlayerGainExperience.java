package io.github.goldeneas.miningrestrictions.events;

import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerGainExperience implements Listener {
    private final Database database;
    private final ExperienceHelper experienceHelper;

    public PlayerGainExperience(Database database, ExperienceHelper experienceHelper) {
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
    }

}
