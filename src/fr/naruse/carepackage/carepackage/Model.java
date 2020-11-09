package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

   private final String name;
   private final List<BlockInfo> blockInfos;
   private final int radius;
   private ParticleInfo[] particleInfos;
   private final HashMap<ModelProperty, Object> propertyMap = Maps.newHashMap();

    public Model(String name, List<BlockInfo> blockInfos, int radius, ParticleInfo[] particleInfos,
                 int particleViewRadius, int soundBarrierEffectRadius, double speedReducer, int randomXZSpawnRange,
                 int secondBeforeRemove, int timeBeforeBarrierEffect){
        this.name = name;
        this.blockInfos = blockInfos;
        this.radius = radius;
        this.particleInfos = particleInfos;
        setProperty(ModelProperty.PARTICLE_VIEW_RADIUS, particleViewRadius);
        setProperty(ModelProperty.SOUND_BARRIER_EFFECT_RADIUS, soundBarrierEffectRadius);
        setProperty(ModelProperty.SPEED_REDUCER, speedReducer > 50 ? 50 : speedReducer);
        setProperty(ModelProperty.RANDOM_XZ_SPAWN_RANGE, randomXZSpawnRange);
        setProperty(ModelProperty.SECOND_BEFORE_REMOVE, secondBeforeRemove);
        setProperty(ModelProperty.TIME_BEFORE_BARRIER_EFFECT, timeBeforeBarrierEffect);
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

    public String toJson(Gson gson) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("radius", radius+"");
        map.put("speedReducer", getProperty(ModelProperty.SPEED_REDUCER)+"");
        map.put("particleViewRadius", getProperty(ModelProperty.PARTICLE_VIEW_RADIUS)+"");
        map.put("randomXZSpawnRange", getProperty(ModelProperty.RANDOM_XZ_SPAWN_RANGE)+"");
        map.put("secondBeforeRemove", getProperty(ModelProperty.SECOND_BEFORE_REMOVE)+"");
        map.put("soundBarrierEffectRadius", getProperty(ModelProperty.SOUND_BARRIER_EFFECT_RADIUS)+"");
        map.put("timeBeforeBarrierEffect", getProperty(ModelProperty.TIME_BEFORE_BARRIER_EFFECT)+"");

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

    public <T> T getProperty(ModelProperty property){
        return (T) propertyMap.get(property);
    }

    public void setProperty(ModelProperty property, Object obj) {
        propertyMap.put(property, obj);
    }

    public void setParticleInfos(ParticleInfo[] particleInfos) {
        this.particleInfos = particleInfos;
    }

    public enum ModelProperty {
        PARTICLE_VIEW_RADIUS("particleViewRadius", Integer.class),
        SOUND_BARRIER_EFFECT_RADIUS("soundBarrierEffectRadius", Integer.class),
        SPEED_REDUCER("speedReducer", Double.class),
        RANDOM_XZ_SPAWN_RANGE("randomXZSpawnRange", Integer.class),
        SECOND_BEFORE_REMOVE("secondBeforeRemove", Integer.class),
        TIME_BEFORE_BARRIER_EFFECT("timeBeforeBarrierEffect", Integer.class),
        ;

        private String name;
        private Class clazz;
        ModelProperty(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }
    }
}
