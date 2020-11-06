package fr.naruse.carepackage.carepackage;

import org.bukkit.Material;

public class BlockInfo {

    private final Material material;
    private final byte data;
    private final int x;
    private final int y;
    private final int z;

    public BlockInfo(Material material, byte data, int x, int y, int z) {
        this.material = material;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public byte getData() {
        return data;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Material getMaterial() {
        return material;
    }
}
