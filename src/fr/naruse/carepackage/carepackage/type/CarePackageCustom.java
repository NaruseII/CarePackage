package fr.naruse.carepackage.carepackage.type;

import fr.naruse.carepackage.carepackage.*;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.Inventory;

public class CarePackageCustom extends CarePackage {

    protected final Model model;

    public CarePackageCustom(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory, Model model,
                             int money, Schedule schedule) {
        super(pl, name, type, destination, inventory, money, schedule);
        this.model = model;
        setSpawnLocation();
        if(model.getParticleInfos() != null){
            this.boosterParticles = model.getParticleInfos();
        }
    }

    @Override
    protected void buildEntities() {
        double distance = Double.MAX_VALUE;
        for (int i = 0; i < model.getBlockInfos().size(); i++) {
            BlockInfo blockInfo = model.getBlockInfos().get(i);
            if(blockInfo.getMaterial() != Material.BARRIER){
                Location location = spawn.clone().add(blockInfo.getX(), blockInfo.getY(), blockInfo.getZ());
                Entity e;
                if(blockInfo.getYaw() != -1){
                    location.setYaw(blockInfo.getYaw());
                    e = createArmorStand(location, blockInfo.getMaterial(), blockInfo.getData());
                }else{
                    e = createFallingBlock(location, blockInfo.getMaterial(), blockInfo.getData());
                }
                double d = Utils.distanceXZ(e.getLocation(), spawn);
                if(d < distance){
                    distance = d;
                    closestEntityForSoundBarrier = e;
                }
            }
        }
        loadBoosters();
    }
    
    protected void loadBoosters(){
        for (int i = 0; i < model.getBlockInfos().size(); i++) {
            BlockInfo blockInfo = model.getBlockInfos().get(i);
            if(blockInfo.getMaterial() == Material.BARRIER){
                Location loc = spawn.clone().add(blockInfo.getX(), blockInfo.getY(), blockInfo.getZ());
                loc.add(0, 1, 0);
                for (Entity nearbyEntity : loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5)) {
                    if(nearbyEntity instanceof FallingBlock){
                        boosters.add(nearbyEntity);
                    }
                }
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
        return model.getRadius();
    }

    @Override
    protected int getParticleViewRadius() {
        return model.getProperty(Model.ModelProperty.PARTICLE_VIEW_RADIUS);
    }

    @Override
    protected int getSoundBarrierEffectRadius() {
        return model.getProperty(Model.ModelProperty.SOUND_BARRIER_EFFECT_RADIUS);
    }

    @Override
    protected double getSpeedReducer() {
        return model.getProperty(Model.ModelProperty.SPEED_REDUCER);
    }

    @Override
    protected int getRandomXZSpawnRange() {
        return model.getProperty(Model.ModelProperty.RANDOM_XZ_SPAWN_RANGE);
    }

    @Override
    protected int getSecondBeforeRemove() {
        return model.getProperty(Model.ModelProperty.SECOND_BEFORE_REMOVE);
    }

    @Override
    protected int getTimeBeforeBarrierEffect() {
        return model.getProperty(Model.ModelProperty.TIME_BEFORE_BARRIER_EFFECT);
    }
}
