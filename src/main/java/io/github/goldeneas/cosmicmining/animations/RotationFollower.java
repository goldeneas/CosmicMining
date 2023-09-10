package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class RotationFollower extends Animation {
    private final ArrayList<ArmorStand> armorStands;

    private int step;

    public RotationFollower(Player player, CosmicMining _plugin) {
        super(player, _plugin);

        armorStands = new ArrayList<>();
        addArmorStands(4, Material.PRISMARINE_BRICKS);

        setRadius(2);
        setPeriod(1L);
        setMaxLifetime(10);
    }

    @Override
    protected void update() {
        double yaw = getPlayer().getLocation().getYaw();

        // TODO: possible optimization -> call cos and sin inverted
        double angle = Math.toRadians(yaw - 180);
        double blocksSpacing = (2.0 * getRadius())/armorStands.size();

        double currentSpacingVertical = 0.0;
        double currentSpacingHorizontal = 0.0;

        for(int i = 0; i < armorStands.size(); i ++) {
            Location l = getPlayer().getLocation().clone();

            double blockX = currentSpacingHorizontal * Math.cos(angle);
            double blockZ = currentSpacingHorizontal * Math.sin(angle);

            if(i%2 == 0) {
                l.add(blockX, 0, blockZ);
            } else {
                l.add(-blockX, 0, -blockZ);
                currentSpacingHorizontal += blocksSpacing;
            }

            armorStands.get(i).teleport(l);
        }


        step += 10;
    }

    /*

            Location l = getPlayer().getLocation().clone();
        l.add(-lookDirection.getZ(), 0, lookDirection.getX());

        double spacing = Math.toRadians(360.0/armorStands.size());
        for(int i = 0; i < armorStands.size(); i++) {
            Location tmp = l.clone();

            if(i != 0) {
                double currentSpacing = spacing * i;

                double xPadding = Math.cos(currentSpacing);
                if(tmp.getX() < 0)
                    xPadding = -xPadding + getRadius();
                else
                    xPadding = xPadding - getRadius();

                tmp.add(xPadding, 0, 0);
                //l.setY(l.getY() + Math.sin(currentSpacing));
            }

            armorStands.get(i).teleport(tmp);
        }

     */

    @Override
    protected void cleanup() {
        removeArmorStands();
    }

    private void addArmorStands(int amount, Material headMaterial) {
        Location location = getPlayer().getLocation();
        World world = location.getWorld();

        for(int i = 0; i < amount; i++) {
            ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setArms(false);
            armorStand.setBasePlate(false);

            EntityEquipment equip = armorStand.getEquipment();
            equip.setHelmet(new ItemStack(headMaterial));

            armorStands.add(armorStand);
            System.out.println("Spawned armorstand");
        }
    }

    private void removeArmorStands() {
        for(ArmorStand armorStand : armorStands)
            armorStand.remove();
    }

}
