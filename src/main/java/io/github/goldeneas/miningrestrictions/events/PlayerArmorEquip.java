package io.github.goldeneas.miningrestrictions.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.ConfigPaths;
import io.github.goldeneas.miningrestrictions.FeedbackString;
import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class PlayerArmorEquip implements Listener {
    private static MiningRestrictions plugin;

    private final YamlDocument config;
    private final ExperienceHelper experienceHelper;

    public PlayerArmorEquip(MiningRestrictions _plugin, ExperienceHelper experienceHelper) {
        plugin = _plugin;
        config = plugin.getConfig("config.yml");

        this.experienceHelper = experienceHelper;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!e.hasItem())
            return;

        Player player = e.getPlayer();
        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        ItemStack item = e.getItem();
        if(experienceHelper.canUseArmor(player, item))
            return;

        FeedbackString levelTooLow = new FeedbackString(plugin);
        levelTooLow.append("armor-level-too-low").formatDefault(experienceHelper, player);
        player.sendMessage(levelTooLow.get());

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        int slot = e.getSlot();
        boolean isShiftClicked = e.isShiftClick();
        if(!isArmorSlot(slot) && !isShiftClicked)
            return;

        HumanEntity entity = e.getWhoClicked();
        if(!(e.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) entity;
        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        ItemStack cursorItem = isShiftClicked ? e.getCurrentItem() : e.getCursor();
        if(experienceHelper.canUseArmor(player, cursorItem))
            return;

        FeedbackString levelTooLow = new FeedbackString(plugin);
        levelTooLow.append("armor-level-too-low").formatDefault(experienceHelper, player);
        player.sendMessage(levelTooLow.get());

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ItemStack item = e.getOldCursor();
        Set<Integer> slots = e.getInventorySlots();

        HumanEntity entity = e.getWhoClicked();
        if(!(e.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) entity;
        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        if((player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission))
            return;

        for(int slot : slots) {
            if(isArmorSlot(slot))
                break;

            return;
        }

        if(experienceHelper.canUseArmor(player, item))
            return;

        FeedbackString levelTooLow = new FeedbackString(plugin);
        levelTooLow.append("armor-level-too-low").formatDefault(experienceHelper, player);
        player.sendMessage(levelTooLow.get());

        e.setCancelled(true);
    }

    private boolean isArmorSlot(int slot) {
        return slot == 39 || slot == 38 || slot == 37 || slot == 36;
    }

}
