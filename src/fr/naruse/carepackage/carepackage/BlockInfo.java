package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.bukkit.Material;

import java.util.Map;

public class BlockInfo {

    private final Material material;
    private final byte data;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;

    public BlockInfo(Material material, byte data, double x, double y, double z, float yaw) {
        this.material = material;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    public byte getData() {
        return data;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
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
        map.put("yaw", yaw+"");

        return gson.toJson(map);
    }

    public static class Builder {

        public static BlockInfo fromJson(Map<String, Object> map) {

            Material material = Material.valueOf((String) map.get("type"));
            byte data = (byte) Integer.parseInt((String) map.get("data"));
            double x = getDouble((String) map.get("x"));
            double y = getDouble((String) map.get("y"));
            double z = getDouble((String) map.get("z"));

            float yaw = -1;
            if(map.containsKey("yaw")){
                yaw = Float.parseFloat((String) map.get("yaw"));
            }

            return new BlockInfo(material, data, x, y, z, yaw);
        }

        private static double getDouble(String s){
            if(s.contains(".")){
                return Double.parseDouble(s);
            }
            return Integer.parseInt(s);
        }
    }
}
