package io.github.goldeneas.miningrestrictions.events;

import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.helpers.BlockHelper;
import io.github.goldeneas.miningrestrictions.utils.ConfigUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerGainExperience implements Listener {
    private final Database database;
    private final BlockHelper blockHelper;

    public PlayerGainExperience(Database database, BlockHelper blockHelper) {
        this.database = database;
        this.blockHelper = blockHelper;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled())
            return;

        Player player = e.getPlayer();
        if(player.hasPermission(ConfigUtil.BYPASS_PERMISSION))
            return;

        Block block = e.getBlock();
        int exp = blockHelper.getGivenExperience(block);

        database.addLevels(e.getPlayer(), exp);
    }

}
