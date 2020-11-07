package fr.naruse.carepackage.carepackage.type;

import fr.naruse.carepackage.carepackage.*;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class CarePackageCustom extends CarePackage {

    private ParticleInfo[] boosterParticles = new ParticleInfo[]{
            new ParticleInfo(EnumParticle.FLAME, 8, 100),
            new ParticleInfo(EnumParticle.EXPLOSION_LARGE, 1, 5),
            new ParticleInfo(EnumParticle.SMOKE_LARGE, 1, 10),
            new ParticleInfo(EnumParticle.SMOKE_NORMAL, 1, 20),
    };

    private final List<BlockInfo> blockInfos;
    private final int radius;

    public CarePackageCustom(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory, Model model) {
        super(pl, name, type, destination, inventory);
        this.blockInfos = model.getBlockInfos();
        this.radius = model.getRadius();
        if(model.getParticleInfos() != null){
            this.boosterParticles = model.getParticleInfos();
        }
    }

    @Override
    protected void buildEntities() {
        double distance = Double.MAX_VALUE;
        for (int i = 0; i < blockInfos.size(); i++) {
            BlockInfo blockInfo = blockInfos.get(i);
            if(blockInfo.getMaterial() != Material.BARRIER){
                Entity e = createFallingBlock(spawn.clone().add(blockInfo.getX(), blockInfo.getY(), blockInfo.getZ()), blockInfo.getMaterial(), blockInfo.getData());
                double d = Utils.distanceXZ(e.getLocation(), spawn);
                if(d < distance){
                    distance = d;
                    closestEntityForSoundBarrier = e;
                }
            }
        }
        for (int i = 0; i < blockInfos.size(); i++) {
            BlockInfo blockInfo = blockInfos.get(i);
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
        return radius;
    }
}
