package io.github.goldeneas.miningrestrictions.events;

import io.github.goldeneas.miningrestrictions.Database;
import io.github.goldeneas.miningrestrictions.helpers.ItemHelper;
import io.github.goldeneas.miningrestrictions.utils.ConfigUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.OptionalInt;

public class PlayerCancelMining implements Listener {
    private final Database database;
    private final ItemHelper itemHelper;

    public PlayerCancelMining(Database database, ItemHelper itemHelper) {
        this.database = database;
        this.itemHelper = itemHelper;
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if(e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if(item == null)
            return;

        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(ConfigUtil.BYPASS_PERMISSION))
            return;

        int requiredLevel = itemHelper.getRequiredLevel(item);
        OptionalInt playerLevel = database.getLevels(player);

        if(playerLevel.isEmpty())
            database.setLevel(player, 0);

        boolean shouldCancel = requiredLevel > playerLevel.orElse(0);
        if(shouldCancel)
            player.sendMessage("Your level is too low!");

        e.setCancelled(shouldCancel);
    }
}
