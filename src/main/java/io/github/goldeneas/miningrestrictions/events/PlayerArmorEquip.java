package io.github.goldeneas.miningrestrictions.events;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.miningrestrictions.ConfigPaths;
import io.github.goldeneas.miningrestrictions.FeedbackString;
import io.github.goldeneas.miningrestrictions.MiningRestrictions;
import io.github.goldeneas.miningrestrictions.helpers.ExperienceHelper;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
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
        if(hasBypassPermission(player))
            return;

        ItemStack item = e.getItem();
        if(experienceHelper.canUseArmor(player, item))
            return;

        denyArmorUsage(player, e);
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
        if(hasBypassPermission(player))
            return;

        InventoryView inventoryView = player.getOpenInventory();
        InventoryType inventoryType = inventoryView.getType();
        if(inventoryType != InventoryType.CRAFTING)
            return;

        ItemStack cursorItem = isShiftClicked ? e.getCurrentItem() : e.getCursor();
        if(experienceHelper.canUseArmor(player, cursorItem))
            return;

        denyArmorUsage(player, e);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ItemStack item = e.getOldCursor();
        Set<Integer> slots = e.getInventorySlots();

        HumanEntity entity = e.getWhoClicked();
        if(!(e.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) entity;
        if(hasBypassPermission(player))
            return;

        for(int slot : slots) {
            if(isArmorSlot(slot))
                break;

            return;
        }

        if(experienceHelper.canUseArmor(player, item))
            return;

        denyArmorUsage(player, e);
    }

    @EventHandler
    public void onItemDispense(BlockDispenseEvent e) {
        if(e.isCancelled())
            return;

        ItemStack item = e.getItem();
        if(!isArmor(item))
            return;

        e.setCancelled(true);
    }

    private void denyArmorUsage(Player player, Cancellable e) {
        new FeedbackString(plugin)
                .append("armor-level-too-low")
                .formatDefault(experienceHelper, player)
                .playSound(Sound.BLOCK_DEEPSLATE_BREAK)
                .sendTo(player);

        e.setCancelled(true);
    }

    private boolean isArmorSlot(int slot) {
        return slot == 39 || slot == 38 || slot == 37 || slot == 36;
    }

    private boolean hasBypassPermission(Player player) {
        String bypassPermission = config.getString(ConfigPaths.BYPASS_PERMISSION_PATH);
        return (player.getGameMode() == GameMode.CREATIVE) || player.hasPermission(bypassPermission);
    }

    private boolean isArmor(ItemStack item) {
        Material m = item.getType();
        String name = m.toString();

        return name.endsWith("_HELMET")
                || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS");
    }
}
