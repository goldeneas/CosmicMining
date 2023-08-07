package io.github.goldeneas.miningrestrictions.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.ConfigPaths;
import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.FeedbackString;
import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.OptionalInt;

public class PlayerCancelMining implements Listener {
    private static MiningRestrictions plugin;

    private final Database database;
    private final YamlDocument config;
    private final ExperienceHelper experienceHelper;

    public PlayerCancelMining(MiningRestrictions _plugin, Database database, ExperienceHelper experienceHelper) {
        plugin = _plugin;
        config = plugin.getConfig("config.yml");

        this.database = database;
        this.experienceHelper = experienceHelper;
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if(e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if(item == null)
            return;

        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        int requiredLevel = experienceHelper.getRequiredLevelForItem(item);
        OptionalInt playerLevel = database.getLevel(player);

        if(playerLevel.isEmpty())
            database.setLevel(player, 0);

        boolean shouldCancel = requiredLevel > playerLevel.orElse(0);
        if(!shouldCancel)
            return;

        FeedbackString levelTooLow = new FeedbackString(plugin);
        levelTooLow.append("level_too_low").formatDefault(experienceHelper, player);
        player.sendMessage(levelTooLow.get());

        e.setCancelled(true);
    }
}
