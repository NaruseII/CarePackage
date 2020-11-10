package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import fr.naruse.carepackage.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public class ParticleInfo {

    private final Object particle;
    private final String particleName;
    private int count;
    private int originalCount;
    private final int percentage;

    private float xOffset = 0.2f;
    private float yOffset = 1f;
    private float zOffset = 0.2f;
    private int yReduced = 1;
    private float speed = 0;
    private boolean canBoost = false;

    public ParticleInfo(String particleName, int count, int percentage) {
        this.particleName = particleName;
        this.count = count;
        this.originalCount = count;
        this.particle = ParticleUtils.fromName(particleName);
        this.percentage = percentage;
        if(particleName.equalsIgnoreCase("FLAME")){
            canBoost = true;
        }
    }

    public ParticleInfo(String particleName, int count, int percentage, float xOffset, float yOffset, float zOffset, float speed, int yReduced, boolean boost) {
       this(particleName, count, percentage);
       this.xOffset = xOffset;
       this.yOffset = yOffset;
       this.zOffset = zOffset;
       this.yReduced = yReduced;
       this.speed = speed;
       this.canBoost = boost;
    }

    public void playParticle(Location location, int particleViewRadius) {
        Object object = ParticleUtils.buildPacket(particle, location.getX(), location.getY(), location.getZ(), xOffset, yOffset, zOffset, speed, count, yReduced);
        for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location, particleViewRadius, particleViewRadius, particleViewRadius)) {
            if(nearbyEntity instanceof Player){
                ParticleUtils.sendPacket(nearbyEntity, object);
            }
        }
    }

    public boolean canBoost() {
        return canBoost;
    }

    public void setCount(int count) {
        if(count == -1){
            this.count = originalCount;
        }else{
            this.count = count;
        }
    }

    public int getCount() {
        return count;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getParticleName() {
        return particleName;
    }

    public String toJson(Gson gson) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("type", particleName);
        map.put("count", count+"");
        map.put("percentage", percentage+"");
        map.put("xOffset", xOffset+"");
        map.put("yOffset", yOffset+"");
        map.put("zOffset", zOffset+"");
        map.put("speed", speed+"");
        map.put("yReduced", yReduced+"");
        map.put("canBoost", canBoost+"");

        return gson.toJson(map);
    }

    public static class Builder {

        public static ParticleInfo fromJson(Map<String, Object> map) {

            String particle = (String) map.get("type");
            int count = Integer.valueOf((String) map.get("count"));
            int percentage = Integer.valueOf((String) map.get("percentage"));

            float xOffset = Float.valueOf((String) map.get("xOffset"));
            float yOffset = Float.valueOf((String) map.get("yOffset"));
            float zOffset = Float.valueOf((String) map.get("zOffset"));
            float speed = Float.valueOf((String) map.get("speed"));
            int yReduced = Integer.valueOf((String) map.get("yReduced"));
            boolean canBoost = Boolean.valueOf((String) map.get("canBoost"));

            return new ParticleInfo(particle, count, percentage, xOffset, yOffset, zOffset, speed, yReduced
            , canBoost);
        }

        public static ParticleInfo fromString(String format){
            String particle = "FLAME";
            int count = 1;
            int percentage = 100;
            float xOffset = 0.2f;
            float yOffset = 1f;
            float zOffset = 0.2f;
            int yReduced = 1;
            float speed = 0;
            boolean boost = false;
            for (String s : format.replace("{", "").replace("}", "").split(",")) {
                String[] args = s.split(":");
                switch (args[0]){
                    case "type":
                        particle = args[1].toUpperCase();
                        break;
                    case "count":
                        count = Integer.valueOf(args[1]);
                        break;
                    case "percentage":
                        percentage = Integer.valueOf(args[1]);
                        break;
                    case "xOffset":
                        xOffset = Float.valueOf(args[1]);
                        break;
                    case "yOffset":
                        yOffset = Float.valueOf(args[1]);
                        break;
                    case "zOffset":
                        zOffset = Float.valueOf(args[1]);
                        break;
                    case "speed":
                        speed = Float.valueOf(args[1]);
                        break;
                    case "yReduced":
                        yReduced = Integer.valueOf(args[1]);
                        break;
                    case "boost":
                        boost = Boolean.valueOf(args[1]);
                        break;
                }
            }
            return new ParticleInfo(particle, count, percentage, xOffset, yOffset, zOffset, speed, yReduced, boost);
        }
    }
}
