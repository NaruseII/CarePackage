package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public abstract class CarePackage extends BukkitRunnable implements Listener {

    protected final static Random RANDOM = new Random();

    protected final CarePackagePlugin pl;
    protected final CarePackageType type;
    protected final Location destination;
    protected final String name;
    protected final Inventory inventory;

    protected Location spawn;
    private Vector vector;
    protected boolean isSpawned = false;
    protected boolean isLanded = false;
    protected final List<Entity> entities = Lists.newArrayList();
    protected final List<Entity> boosters = Lists.newArrayList();

    public CarePackage(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory) {
        this.pl = pl;
        this.name = name;
        this.type = type;
        this.destination = destination;
        this.inventory = inventory;

        this.spawn = destination.clone();
        spawn.setY(spawn.getWorld().getMaxHeight()+50);
        spawn.add(RANDOM.nextBoolean() ? RANDOM.nextInt(35) : -RANDOM.nextInt(35), 0
                , RANDOM.nextBoolean() ? RANDOM.nextInt(35) : -RANDOM.nextInt(35));
        this.vector = destination.toVector().subtract(spawn.toVector()).divide(new Vector(1000, 1000, 1000));

        Bukkit.getPluginManager().registerEvents(this, pl);
        this.runTaskTimer(pl, 1, 1);
    }

    private int tickCount = 0;
    private int secondCount = 0;
    private int secondCountLanded = 0;
    @Override
    public void run() {
        if(!isSpawned){
            return;
        }
        if(tickCount >= 20){
            tickCount = 0;
            secondCount++;
            this.onSecond();
            if(!isLanded){
                //this.targetDestination();
            }else{
                if(secondCountLanded >= 60){
                    destroy();
                    return;
                }else{
                    secondCountLanded++;
                }
            }
            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).setTicksLived(1);
            }
        }else{
            tickCount++;
        }
        this.onTick();
        //playBoosterParticles();
    }

    protected abstract void buildEntities();

    protected abstract void onTick();

    protected abstract void onSecond();

    protected abstract ParticleInfo[] getBoosterParticle();

    protected abstract double getRadius();

    protected void targetDestination() {
        if(entities.size() == 0){
            return;
        }
        Location location = entities.get(0).getLocation();
        if(secondCount < 5){
            setSpeed(vector.clone().multiply(4));
        }else if(secondCount == 5 || secondCount == 15){
            playSoundBarrierBreakParticles(location);
            if(secondCount == 5){
                reduceSpeedTo(vector.clone().multiply(3));
            }else{
                reduceSpeedTo(vector.clone().multiply(2));
            }
        }else if(secondCount < 10){
            setSpeed(vector.clone().multiply(3));
        }else{
            if(location.distance(destination) > 25){
                getBoosterParticle()[0].setCount(12);
                reduceSpeedTo(vector.clone().multiply(2));
            }else if(location.distance(destination) > 20){
                getBoosterParticle()[0].setCount(15);
                reduceSpeedTo(vector.clone().multiply(1));
            }else if(location.distance(destination) > 15){
                getBoosterParticle()[0].setCount(20);
                reduceSpeedTo(vector.clone().multiply(0.8));
            }else if(location.distance(destination) > 12){
                reduceSpeedTo(vector.clone().multiply(0.5));
            }else if(location.distance(destination) > 10){
                reduceSpeedTo(vector.clone().multiply(0.3));
            }else{
                setSpeed(vector.clone().multiply(0.2));
            }

            Location closest = location.clone();
            double d = closest.distanceSquared(destination);
            for (Entity entity : entities) {
                if(entity.getLocation().distance(destination) < d){
                    closest = entity.getLocation();
                }
            }
            if(destination.distance(closest) <= 6){
                getBoosterParticle()[0].setCount(6);
                setSpeed(new Vector(0, 0, 0));
                isLanded = true;
            }
        }
    }

    private boolean isReducingSpeed = false;
    private void reduceSpeedTo(Vector vector){
        isReducingSpeed = true;
        if(entities.size() == 0){
            isReducingSpeed = false;
            return;
        }
        Vector currentVector = null;
        for (Entity entity : entities) {
            if(entity instanceof FallingBlock){
                currentVector = entity.getVelocity();
            }
        }
        if(currentVector == null){
            isReducingSpeed = false;
            return;
        }
        if(currentVector.getY() < vector.getY()){
            for (Entity entity : entities) {
                entity.setVelocity(currentVector.clone().add(new Vector(0, 0.005, 0)));
            }
        }else{
            isReducingSpeed = false;
            setSpeed(vector);
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
           reduceSpeedTo(vector);
        }, 1);
    }

    private void setSpeed(Vector vector){
        if(isReducingSpeed){
            return;
        }
        for (Entity entity : entities) {
            entity.setVelocity(vector);
        }
    }

    public void spawn() {
        if(isSpawned){
            return;
        }
        this.isSpawned = true;
        if(!canSpawn()){
            isSpawned = false;
            return;
        }
        buildEntities();
    }

    public void destroy(){
        this.isSpawned = false;
        this.isLanded = false;
        for (Entity e : entities) {
            e.remove();
        }
        entities.clear();
        entities.clear();
    }

    protected void playSoundBarrierBreakParticles(Location currentLocation){
        for (int i = 0; i < 50; i++) {
            final int r = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
                for (Block block : Utils.getCircle(currentLocation, r)) {
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_LARGE, true, (float) block.getX(), (float) block.getY()-1,
                            (float) block.getZ(), 1, 0, 1, 0, 1);
                    for (Entity nearbyEntity : currentLocation.getWorld().getNearbyEntities(currentLocation, 100, 100, 100)) {
                        if(nearbyEntity instanceof Player){
                            ((CraftPlayer) nearbyEntity).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }
            }, 1*i);
        }
    }

    protected void playBoosterParticles() {
        for (int i = 0; i < boosters.size(); i++) {
            Location location = boosters.get(i).getLocation();
            for (ParticleInfo particleInfo : getBoosterParticle()) {
                if(particleInfo.getPercentage() != 100 && RANDOM.nextInt(100)+1 > particleInfo.getPercentage()){
                    continue;
                }
                PacketPlayOutWorldParticles packet = particleInfo.getParticlePacket(location);
                for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 60, 60, 60)) {
                    if(nearbyEntity instanceof Player){
                        ((CraftPlayer) nearbyEntity).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            }
        }
    }

    protected boolean canSpawn(){
        Location location = destination.clone();
        for (int y = location.getBlockY(); y < location.getWorld().getMaxHeight(); y++) {
            for (int i = 0; i < getRadius(); i++) {
                for (Block block : Utils.getCircle(location, i)) {
                    if(block.getType() != Material.AIR){
                        return false;
                    }
                }
            }
            location.add(0, 1, 0);
        }
        return true;
    }

    public void disable() {
        cancel();
        destroy();
    }

    protected ArmorStand createArmorStand(Location location, Material m){
        return this.createArmorStand(location, m, (byte) 0);
    }

    protected ArmorStand createArmorStand(Location location, Material m, byte data){
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(m, 1, data));
        armorStand.setGravity(false);
        entities.add(armorStand);

        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, Material.BARRIER, (byte) 0);
        fallingBlock.setGravity(false);
        fallingBlock.setPassenger(armorStand);
        entities.add(fallingBlock);

        return armorStand;
    }

    protected FallingBlock createFallingBlock(Location location, Material m){
        return this.createFallingBlock(location, m, (byte) 0);
    }

    protected FallingBlock createFallingBlock(Location location, Material m, byte data){
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, m, data);
        fallingBlock.setGravity(false);
        entities.add(fallingBlock);
        return fallingBlock;
    }

    @EventHandler
    public void interact(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() != null && entities.contains(e.getRightClicked())){
            e.setCancelled(true);
            if(isLanded){
                e.getPlayer().openInventory(inventory);
            }
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent e){
        if(isSpawned && Utils.distanceXZ(e.getBlockPlaced().getLocation(), destination) <= getRadius()){
            e.setCancelled(true);
        }
    }

    public String getName() {
        return name;
    }
}
