package fr.naruse.carepackage.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;

public final class ParticleUtils {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    private static final double DOUBLE_VERSION;
    static {
        String version = VERSION.replace("v", "").replace("R1", "").replace("R2", "").replace("_", ".").replace("R3", "");
        DOUBLE_VERSION = Double.valueOf(version.trim().substring(0, version.length()-2));
    }

    private static Class<?> getNMSClass(String nmsClassString) {
        try{
            String name = "net.minecraft.server." + VERSION + nmsClassString;
            Class<?> nmsClass = Class.forName(name);
            return nmsClass;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Class getParticleClass(){
        if(DOUBLE_VERSION >= 1.13){
            return getNMSClass("Particles");
        }
        return getNMSClass("EnumParticle");
    }

    public static Object fromName(String particleName) {
        try {
            return getParticleClass().getField(particleName.toUpperCase()).get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object buildPacket(Object particle, double x, double y, double z, float xOffset, float yOffset, float zOffset, float speed, int count, int yReduced){
        try{
            if(DOUBLE_VERSION >= 1.13){
                Constructor constructor = getNMSClass("PacketPlayOutWorldParticles").getConstructor(getNMSClass("ParticleParam"), boolean.class, double.class,
                        double.class, double.class, float.class, float.class, float.class, float.class, int.class);
                return constructor.newInstance(particle, true, x, y-yReduced, z, xOffset, yOffset, zOffset, speed, count);
            }
            Constructor constructor = getNMSClass("PacketPlayOutWorldParticles").getConstructor(getNMSClass("EnumParticle"), boolean.class, float.class,
                    float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
            return constructor.newInstance(particle, true, (float) x, (float) y-yReduced, (float) z, xOffset, yOffset, zOffset, speed, count, new int[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Entity nearbyEntity, Object object) {
        try{
            Object craftPlayer = nearbyEntity.getClass().getMethod("getHandle").invoke(nearbyEntity);
            Object connection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getParticleNameFromNative(String name) {
        if(DOUBLE_VERSION >= 1.13){
            switch (name){
                case "EXPLOSION_LARGE": return "EXPLOSION";
                case "SMOKE_LARGE": return "LARGE_SMOKE";
                case "SMOKE_NORMAL": return "SMOKE";
            }
        }
        return name;
    }
}
