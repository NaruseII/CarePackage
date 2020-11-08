package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class Model {

   private final String name;
   private final List<BlockInfo> blockInfos;
   private final int radius;
   private final ParticleInfo[] particleInfos;
   private final int particleViewRadius;
   private final int soundBarrierEffectRadius;
   private final double speedReducer;
   private final int randomXZSpawnRange;
   private final int secondBeforeRemove;
   private final int timeBeforeBarrierEffect;

    public Model(String name, List<BlockInfo> blockInfos, int radius, ParticleInfo[] particleInfos,
                 int particleViewRadius, int soundBarrierEffectRadius, double speedReducer, int randomXZSpawnRange,
                 int secondBeforeRemove, int timeBeforeBarrierEffect){
        this.name = name;
        this.blockInfos = blockInfos;
        this.radius = radius;
        this.particleInfos = particleInfos;
        this.particleViewRadius = particleViewRadius;
        this.soundBarrierEffectRadius = soundBarrierEffectRadius;
        this.speedReducer = speedReducer;
        this.randomXZSpawnRange = randomXZSpawnRange;
        this.secondBeforeRemove = secondBeforeRemove;
        this.timeBeforeBarrierEffect = timeBeforeBarrierEffect;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public List<BlockInfo> getBlockInfos() {
        return blockInfos;
    }

    public ParticleInfo[] getParticleInfos() {
        return particleInfos;
    }

    public double getSpeedReducer() {
        return speedReducer;
    }

    public int getParticleViewRadius() {
        return particleViewRadius;
    }

    public int getRandomXZSpawnRange() {
        return randomXZSpawnRange;
    }

    public int getSecondBeforeRemove() {
        return secondBeforeRemove;
    }

    public int getSoundBarrierEffectRadius() {
        return soundBarrierEffectRadius;
    }

    public int getTimeBeforeBarrierEffect() {
        return timeBeforeBarrierEffect;
    }

    public String toJson(Gson gson) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("radius", radius+"");
        map.put("speedReducer", speedReducer+"");
        map.put("particleViewRadius", particleViewRadius+"");
        map.put("randomXZSpawnRange", randomXZSpawnRange+"");
        map.put("secondBeforeRemove", secondBeforeRemove+"");
        map.put("soundBarrierEffectRadius", soundBarrierEffectRadius+"");
        map.put("timeBeforeBarrierEffect", timeBeforeBarrierEffect+"");

        StringBuilder builder = new StringBuilder();
        if(particleInfos != null){
            if(particleInfos.length != 0){
                builder.append(particleInfos[0].toJson(gson));
            }
            for (int i = 1; i < particleInfos.length; i++) {
                builder.append(particleInfos[i].toJson(gson));
            }
            map.put("particles", builder.toString());
        }

        for (int i = 0; i < blockInfos.size(); i++) {
            map.put(i+"", blockInfos.get(i).toJson(gson));
        }

        return gson.toJson(map);
    }
}
