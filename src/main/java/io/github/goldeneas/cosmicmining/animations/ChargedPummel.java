package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ChargedPummel extends Animation {
    private final ArrayList<ArmorStand> armorStands;

    private int step;

    public ChargedPummel(Location location, CosmicMining _plugin) {
        super(location, _plugin);

        armorStands = new ArrayList<>();
        createArmorStands(1, Material.PRISMARINE_BRICKS);

        setRadius(1);
        setPeriod(1L);
        setMaxLifetime(5);
    }

    @Override
    protected void update() {
        double radians = Math.toRadians(step);

        double x = Math.cos(radians);
        double y = Math.sin(radians);

        Location tmp = getLocation().clone();
        tmp.add(x, y, 0);

        for(ArmorStand armorStand : armorStands) {
            armorStand.teleport(tmp);
        }

        step += 10;
    }

    @Override
    protected void cleanup() {
        System.out.println("called cleanup");
        removeArmorStands();
    }

    private void createArmorStands(int amount, Material headMaterial) {
        Location location = getLocation();
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
        }
    }

    private void removeArmorStands() {
        for(ArmorStand armorStand : armorStands)
            armorStand.remove();
    }


}
