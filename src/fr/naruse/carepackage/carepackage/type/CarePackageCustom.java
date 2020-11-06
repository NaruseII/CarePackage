package fr.naruse.carepackage.carepackage.type;

import fr.naruse.carepackage.carepackage.BlockInfo;
import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.carepackage.ParticleInfo;
import fr.naruse.carepackage.main.CarePackagePlugin;
import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CarePackageCustom extends CarePackage implements Cloneable {

    private final ParticleInfo[] BOOSTERS_PARTICLES = new ParticleInfo[]{
            new ParticleInfo(EnumParticle.FLAME, 8, 100),
            new ParticleInfo(EnumParticle.EXPLOSION_LARGE, 1, 5),
            new ParticleInfo(EnumParticle.SMOKE_LARGE, 1, 10),
            new ParticleInfo(EnumParticle.SMOKE_NORMAL, 1, 20),
    };

    private List<BlockInfo> blockInfos;
    private int radius;

    public CarePackageCustom(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory) {
        super(pl, name, type, destination, inventory);
    }

    public CarePackageCustom(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory, List<BlockInfo> blockInfos,
                             int radius) {
        this(pl, name, type, destination, inventory);
        this.blockInfos = blockInfos;
        this.radius = radius;
    }

    @Override
    protected void buildEntities() {
        for (int i = 0; i < blockInfos.size(); i++) {
            BlockInfo blockInfo = blockInfos.get(i);
            createFallingBlock(spawn.clone().add(blockInfo.getX(), blockInfo.getY(), blockInfo.getZ()), blockInfo.getMaterial(), blockInfo.getData());
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
        return BOOSTERS_PARTICLES;
    }

    @Override
    protected double getRadius() {
        return radius;
    }

    public void setBlockInfos(List<BlockInfo> blockInfos){
        this.blockInfos = blockInfos;
    }

    @Override
    public CarePackageCustom clone(){
        Inventory inv = Bukkit.createInventory(null, 9*6, pl.getMessageManager().get("inventory.get.name"));
        for (int o = 0; o < 9*6; o++) {
            ItemStack item = inventory.getItem(o);
            if(item != null) {
                inv.setItem(o, item.clone());
            }
        }
        return new CarePackageCustom(pl, name, type, destination, inv, blockInfos, radius);
    }
}
