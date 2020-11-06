package fr.naruse.carepackage.carepackage;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;

public class ParticleInfo {

    private final EnumParticle particle;
    private int count;
    private final int percentage;

    private float x = 0.2f;
    private float y = 1f;
    private float z = 0.2f;
    private int yOffset = 1;

    public ParticleInfo(EnumParticle particle, int count, int percentage) {
        this.count = count;
        this.particle = particle;
        this.percentage = percentage;
    }

    public PacketPlayOutWorldParticles getParticlePacket(Location location){
        return new PacketPlayOutWorldParticles(getParticle(), true, (float) location.getX(), (float) location.getY()-yOffset,
                (float) location.getZ(), x, y, z, 0, getCount());
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setCount(int count) {
        this.count = count;
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
}
