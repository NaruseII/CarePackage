package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;

import java.util.Map;

public class ParticleInfo {

    private final EnumParticle particle;
    private int count;
    private int originalCount;
    private final int percentage;

    private float xOffset = 0.2f;
    private float yOffset = 1f;
    private float zOffset = 0.2f;
    private int yReduced = 1;
    private float speed = 0;
    private boolean canBoost = false;

    public ParticleInfo(EnumParticle particle, int count, int percentage) {
        this.count = count;
        this.originalCount = count;
        this.particle = particle;
        this.percentage = percentage;
        if(particle == EnumParticle.FLAME){
            canBoost = true;
        }
    }

    public ParticleInfo(EnumParticle particle, int count, int percentage, float xOffset, float yOffset, float zOffset, float speed, int yReduced, boolean boost) {
       this(particle, count, percentage);
       this.xOffset = xOffset;
       this.yOffset = yOffset;
       this.zOffset = zOffset;
       this.yReduced = yReduced;
       this.speed = speed;
       this.canBoost = boost;
    }

    public PacketPlayOutWorldParticles getParticlePacket(Location location){
        return new PacketPlayOutWorldParticles(getParticle(), true, (float) location.getX(), (float) location.getY()-yReduced,
                (float) location.getZ(), xOffset, yOffset, zOffset, speed, getCount());
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

    public EnumParticle getParticle() {
        return particle;
    }

    public String toJson(Gson gson) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("count", count);
        map.put("percentage", percentage);
        map.put("xOffset", xOffset);
        map.put("yOffset", yOffset);
        map.put("zOffset", zOffset);
        map.put("speed", speed);
        map.put("yReduced", yReduced);
        map.put("canBoost", canBoost);

        return gson.toJson(map);
    }

    public static class Builder {

        public static ParticleInfo fromJson(Map<String, Object> map) {

            EnumParticle particle = EnumParticle.valueOf((String) map.get("type"));
            int count = (int) map.get("count");
            int percentage = (int) map.get("percentage");

            float xOffset = (float) (int) map.get("xOffset");
            float yOffset = (float) (int) map.get("yOffset");
            float zOffset = (float) (int) map.get("zOffset");
            float speed = (float) (int) map.get("speed");
            int yReduced = (int) map.get("yReduced");
            boolean canBoost = (boolean) map.get("canBoost");

            return new ParticleInfo(particle, count, percentage, xOffset, yOffset, zOffset, speed, yReduced
            , canBoost);
        }
    }
}
