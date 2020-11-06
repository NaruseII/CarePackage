package fr.naruse.carepackage.carepackage.type;

import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.carepackage.ParticleInfo;
import fr.naruse.carepackage.main.CarePackagePlugin;
import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class CarePackageSimple extends CarePackage {

    private final ParticleInfo[] BOOSTERS_PARTICLES = new ParticleInfo[]{
            new ParticleInfo(EnumParticle.FLAME, 8, 100),
            new ParticleInfo(EnumParticle.EXPLOSION_LARGE, 1, 5),
            new ParticleInfo(EnumParticle.SMOKE_LARGE, 1, 10),
            new ParticleInfo(EnumParticle.SMOKE_NORMAL, 1, 20),
    };

    public CarePackageSimple(CarePackagePlugin pl, String name, Location destination, Inventory inventory) {
        super(pl, name, CarePackageType.SIMPLE, destination, inventory);
    }

    @Override
    protected void buildEntities() {
        Location origin = destination.clone().add(0, 8, 0);
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

        targetDestination();
    }

    @Override
    protected void onTick() {

    }

    @Override
    protected void onSecond() {

    }

    @Override
    protected ParticleInfo[] getBoosterParticle() {
        return BOOSTERS_PARTICLES;
    }

    @Override
    protected double getRadius() {
        return 4;
    }
}
