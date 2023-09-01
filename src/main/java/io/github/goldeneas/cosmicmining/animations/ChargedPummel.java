package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.utils.MathUtils;
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

public class ChargedPummel extends Animation {
    private final ArrayList<ArmorStand> armorStands;

    private int step;

    public ChargedPummel(Player player, CosmicMining _plugin) {
        super(player, _plugin);

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
        double z = Math.sin(radians);

        Location tmp = getPlayer().getLocation().clone();
        tmp.add(x, y, z);
        tmp.setYaw(0);
        tmp.setPitch(0);

        for(ArmorStand armorStand : armorStands) {
            armorStand.teleport(tmp);
        }

        step += 10;
    }

    @Override
    protected void cleanup() {
        removeArmorStands();
    }

    private void createArmorStands(int amount, Material headMaterial) {
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
        }
    }

    private void removeArmorStands() {
        for(ArmorStand armorStand : armorStands)
            armorStand.remove();
    }


}
