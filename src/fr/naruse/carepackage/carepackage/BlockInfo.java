package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.bukkit.Material;

import java.util.Map;

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

    public String toJson(Gson gson) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("type", material.name());
        map.put("data", data+"");
        map.put("x", x+"");
        map.put("y", y+"");
        map.put("z", z+"");

        return gson.toJson(map);
    }

    public static class Builder {

        public static BlockInfo fromJson(Map<String, Object> map) {

            Material material = Material.valueOf((String) map.get("type"));
            byte data = (byte) Integer.parseInt((String) map.get("data"));
            int x = Integer.valueOf((String) map.get("x"));
            int y = Integer.valueOf((String) map.get("y"));
            int z = Integer.valueOf((String) map.get("z"));

            return new BlockInfo(material, data, x, y, z);
        }
    }
}
