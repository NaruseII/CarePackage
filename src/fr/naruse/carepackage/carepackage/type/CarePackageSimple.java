package fr.naruse.carepackage.carepackage.type;

import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.carepackage.ParticleInfo;
import fr.naruse.carepackage.carepackage.Schedule;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

public class CarePackageSimple extends CarePackage {

    public CarePackageSimple(CarePackagePlugin pl, String name, Location destination, Inventory inventory, int money, Schedule schedule) {
        super(pl, name, CarePackageType.SIMPLE, destination, inventory, money, schedule);
        setSpawnLocation();
    }

    @Override
    protected void buildEntities() {
        Location origin = spawn.clone();
        Location location = origin.clone();
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(-0.28, 0, -0.128);
        location.setYaw(50);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location = origin.clone();
        location.add(0.27, 0, -0.128);
        location.setYaw(-50);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);

        location = origin.clone().add(-0.41, 0, -0.475);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location = origin.clone().add(0.3975, 0, -0.4838);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);

        location = origin.clone();
        location.add(-0.28+0.555, 0, -0.128-0.7);
        location.setYaw(50);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location = origin.clone();
        location.add(0.27-0.555, 0, -0.128-0.7);
        location.setYaw(-50);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location = origin.clone();
        location.add(-0.01, 0, -0.958);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location = origin.clone();
        location.add(0, 0.35, -0.5);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);

        location = origin.clone();
        location.add(0, -0.3, 0.3);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.4, 0.3);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, 0.5+1, 0);
        boosters.add(createFallingBlock(location, Material.DARK_OAK_FENCE));

        location = origin.clone();
        location.add(-0.01, -0.3, -0.958-0.3);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.4, -0.3);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, 0.5+1, 0);
        boosters.add(createFallingBlock(location, Material.DARK_OAK_FENCE));

        location = origin.clone().add(-0.41, 0, -0.475);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(-0.3, -0.3, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(-0.4, -0.4, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, 0.5+1, 0);
        boosters.add(createFallingBlock(location, Material.DARK_OAK_FENCE));

        location = origin.clone();
        location.add(0.3975, 0, -0.4838);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0.3, -0.3, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0.4, -0.4, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, -0.5, 0);
        createArmorStand(location, Material.STAINED_CLAY, (byte) 7);
        location.add(0, 0.5+1, 0);
        boosters.add(createFallingBlock(location, Material.DARK_OAK_FENCE));

        location = origin.clone();
        location.add(0, 3, -0.5);
        createFallingBlock(location, Material.FENCE);

        double distance = Double.MAX_VALUE;
        for (Entity e : entities) {
            double d = Utils.distanceXZ(e.getLocation(), spawn);
            if(d < distance){
                distance = d;
                closestEntityForSoundBarrier = e;
            }
        }
    }

    @Override
    protected void onTick() {

    }

    @Override
    protected void onSecond() {

    }

    @Override
    protected ParticleInfo[] getBoosterParticle() {
        return boosterParticles;
    }

    @Override
    protected double getRadius() {
        return 4;
    }

    @Override
    protected int getParticleViewRadius() {
        return 100;
    }

    @Override
    protected int getSoundBarrierEffectRadius() {
        return 100;
    }

    @Override
    protected double getSpeedReducer() {
        return 0.015;
    }

    @Override
    protected int getRandomXZSpawnRange() {
        return 35;
    }

    @Override
    protected int getSecondBeforeRemove() {
        return 60;
    }

    @Override
    protected int getTimeBeforeBarrierEffect() {
        return 8;
    }
}
