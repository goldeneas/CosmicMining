package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemShield extends Animation{
    private final Material itemType;
    private ArrayList<ItemFrame> itemFrames;

    private int step;

    public ItemShield(Material itemType, Player player, CosmicMining _plugin) {
        super(player, _plugin);

        this.itemType = itemType;
        this.itemFrames = new ArrayList<>();

        createItemFrames(4);

        setRadius(1);
        setPeriod(1L);
        setMaxLifetime(5);
    }

    @Override
    protected void update() {
        double radians = Math.toRadians(step);
        double z = -Math.cos(radians);
        double x = Math.sin(radians);

        for(ItemFrame itemFrame : itemFrames) {
            Location l = getPlayer().getLocation().clone();

            itemFrame.teleport(l.add(x, 0, z));
        }

        step += 10;
    }

    @Override
    protected void cleanup() {

    }

    private void createItemFrames(int amount) {
        Location playerLocation = getPlayer().getLocation().add(0, 0.5, 0);
        World world = playerLocation.getWorld();

        ItemStack item = new ItemStack(itemType);

        for(int i = 0; i < amount; i++) {
            ItemFrame itemFrame = (ItemFrame) world.spawnEntity(playerLocation, EntityType.ITEM_FRAME);
            itemFrame.setVisible(false);
            itemFrame.setItem(item);
            itemFrame.setInvulnerable(true);
            itemFrame.setGravity(false);

            itemFrames.add(itemFrame);
        }
    }
}
