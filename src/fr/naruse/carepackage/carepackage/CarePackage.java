package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.api.event.CarePackageDestroyEvent;
import fr.naruse.carepackage.api.event.CarePackageSpawnEvent;
import fr.naruse.carepackage.api.event.PlayerOpenCarePackageEvent;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.ParticleUtils;
import fr.naruse.carepackage.utils.Utils;
import fr.naruse.carepackage.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.logging.Level;

public abstract class CarePackage extends BukkitRunnable implements Listener {

    protected final static Random RANDOM = new Random();


    protected ParticleInfo[] boosterParticles = new ParticleInfo[]{
            new ParticleInfo(ParticleUtils.getParticleNameFromNative("FLAME"), 8, 100),
            new ParticleInfo(ParticleUtils.getParticleNameFromNative("EXPLOSION_LARGE"), 1, 5),
            new ParticleInfo(ParticleUtils.getParticleNameFromNative("SMOKE_LARGE"), 1, 10),
            new ParticleInfo(ParticleUtils.getParticleNameFromNative("SMOKE_NORMAL"), 1, 20),
    };

    protected final CarePackagePlugin pl;
    protected final CarePackageType type;
    protected final Location destination;
    protected final String name;
    protected final Inventory inventory;
    protected final int money;
    protected final Schedule schedule;

    protected Location spawn;
    protected Vector vector;
    protected boolean isSpawned = false;
    protected boolean isLanded = false;
    protected final List<Entity> entities = Lists.newArrayList();
    protected final List<Entity> boosters = Lists.newArrayList();

    protected Entity closestEntityForSoundBarrier;
    private Entity closestEntity;
    private Player inventoryOpener;

    public CarePackage(CarePackagePlugin pl, String name, CarePackageType type, Location destination, Inventory inventory, int money, Schedule schedule) {
        this.pl = pl;
        this.name = name;
        this.type = type;
        this.destination = destination;
        this.inventory = inventory;
        this.money = money;
        this.schedule = schedule;

        Bukkit.getPluginManager().registerEvents(this, pl);
        this.runTaskTimer(pl, 1, 1);
    }

    private int tickCount = 0;
    private int secondCount = 0;
    private int secondCountLanded = 0;
    private int secondBeforeSpawn = 0;
    @Override
    public void run() {
        if(tickCount >= 20){
            tickCount = 0;
            if(schedule != null){
                if(secondBeforeSpawn >= schedule.getEvery()) {
                    secondBeforeSpawn = 0;
                    if (schedule.getRandomPercentage() >= 100 || RANDOM.nextInt(100) + 1 >= schedule.getRandomPercentage()) {
                        if (spawn() == 0 && schedule.canBroadcast()) {
                            Bukkit.broadcastMessage(pl.getMessageManager().get("carePackagedSpawned", new String[]{"x", "y", "z", "world"},
                                    new String[]{destination.getBlockX() + "", destination.getBlockY() + "", destination.getBlockZ() + "", destination.getWorld().getName()}));
                        }
                    }
                }else{
                    secondBeforeSpawn++;
                }
            }
        }else{
            tickCount++;
        }

        if(!isSpawned){
            return;
        }

        if(tickCount == 0){
            secondCount++;
            this.onSecond();
            if(!isLanded){
                this.targetDestination();
            }else{
                if(secondCountLanded >= getSecondBeforeRemove()){
                    destroy();
                    return;
                }else{
                    secondCountLanded++;
                }
            }
            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).setTicksLived(1);
            }
        }
        this.onTick();
        playBoosterParticles();
        checkCrash();
    }

    protected abstract void buildEntities();

    protected abstract void onTick();

    protected abstract void onSecond();

    protected abstract ParticleInfo[] getBoosterParticle();

    protected abstract double getRadius();

    protected abstract int getParticleViewRadius();

    protected abstract int getSoundBarrierEffectRadius();

    protected abstract double getSpeedReducer();

    protected abstract int getRandomXZSpawnRange();

    protected abstract int getSecondBeforeRemove();

    protected abstract int getTimeBeforeBarrierEffect();

    protected void targetDestination() {
        if(entities.size() == 0){
            return;
        }
        Location location = closestEntity.getLocation();
        double distance = Utils.distanceY(location, destination);

        if(secondCount == getTimeBeforeBarrierEffect()){
            playSoundBarrierBreakParticles();
        }

        if(distance > 50){
            setSpeed(vector.clone().multiply(6));
        }else if(distance > 40){
            reduceSpeedTo(vector.clone().multiply(3));
        }else if(distance > 30){
            reduceSpeedTo(vector.clone().multiply(2));
        }else if(distance > 25){
            setParticleCount(12);
            reduceSpeedTo(vector.clone().multiply(2));
        }else if(distance > 20){
            setParticleCount(15);
            reduceSpeedTo(vector.clone().multiply(1));
        }else if(distance > 15){
            setParticleCount(20);
            reduceSpeedTo(vector.clone().multiply(1));
        }else if(distance > 12){
            reduceSpeedTo(vector.clone().multiply(0.8));
        }else if(distance > 5){
            reduceSpeedTo(vector.clone().multiply(0.3));
        }else if(distance > 2){
            reduceSpeedTo(vector.clone().multiply(0.2));
        }else if(distance > 1.5){
            reduceSpeedTo(vector.clone().multiply(0.1));
        }else if(distance > 1){
            reduceSpeedTo(vector.clone().multiply(0.05));
        }else if(distance > 0.5){
            reduceSpeedTo(vector.clone().multiply(0.025));
        }else {
            setParticleCount(6);
            setSpeed(new Vector(0, 0, 0));
            isLanded = true;
        }
    }

    private void setParticleCount(int count){
        for (ParticleInfo particleInfo : getBoosterParticle()) {
            if(particleInfo.canBoost()){
                particleInfo.setCount(count);
            }
        }
    }

    private void checkCrash(){
        Location location = closestEntity.getLocation();
        double distance = Utils.distanceY(location, destination);
        if(distance < 0.4){
            setParticleCount(6);
            setSpeed(new Vector(0, 0, 0));
            isLanded = true;
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
                break;
            }
        }
        if(currentVector == null){
            isReducingSpeed = false;
            return;
        }
        if(currentVector.getY() < vector.getY()){
            Vector newVector = currentVector.clone().add(new Vector(0, getSpeedReducer(), 0));
            for (Entity entity : entities) {
                entity.setVelocity(newVector);
            }
        }else{
            isReducingSpeed = false;
            setSpeed(vector);
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> reduceSpeedTo(vector), 1);
    }

    private void setSpeed(Vector vector){
        if(isReducingSpeed){
            return;
        }
        for (Entity entity : entities) {
            entity.setVelocity(vector);
        }
    }

    public int spawn() {
        if(isCancelled()){
            return 1;
        }
        if(isSpawned){
            return 1;
        }
        this.isSpawned = true;
        if(!canSpawn()){
            isSpawned = false;
            pl.getLogger().log(Level.INFO, "Can't spawn '"+type.getName()+"'");
            return 2;
        }

        CarePackageSpawnEvent event = new CarePackageSpawnEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()){
            return 2;
        }

        buildEntities();
        this.closestEntity = getClosest();
        return 0;
    }

    public void destroy(){
        Bukkit.getPluginManager().callEvent(new CarePackageDestroyEvent(this));
        this.isSpawned = false;
        this.isLanded = false;
        this.inventoryOpener = null;
        this.secondCountLanded = 0;
        this.secondBeforeSpawn = 0;
        this.closestEntity = null;
        this.inventoryOpener = null;
        this.closestEntityForSoundBarrier = null;
        this.isReducingSpeed = false;
        for (Entity e : entities) {
            e.remove();
        }
        entities.clear();
        boosters.clear();
    }

    protected void playSoundBarrierBreakParticles(){
        if(closestEntityForSoundBarrier == null){
            return;
        }

        for (int i = 0; i < getSoundBarrierEffectRadius(); i++) {
            final int r = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
                for (Block block : Utils.getCircle(closestEntityForSoundBarrier.getLocation(), r)) {
                    Object packet = ParticleUtils.buildPacket(ParticleUtils.fromName(ParticleUtils.getParticleNameFromNative("SMOKE_LARGE")), block.getX(), block.getY()-1,
                            block.getZ(), 1f, 0f, 1f, 0f, 1, 1);
                    for (Entity nearbyEntity : closestEntityForSoundBarrier.getLocation().getWorld().getNearbyEntities(closestEntityForSoundBarrier.getLocation(), getParticleViewRadius(), getParticleViewRadius(), getParticleViewRadius())) {
                        if(nearbyEntity instanceof Player){
                            ParticleUtils.sendPacket(nearbyEntity, packet);
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
                particleInfo.playParticle(location, getParticleViewRadius());
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

    protected void setSpawnLocation(){
        this.spawn = destination.clone();
        spawn.setY(destination.getY()+spawn.getWorld().getMaxHeight());
        spawn.add(RANDOM.nextBoolean() ? RANDOM.nextInt(getRandomXZSpawnRange()) : -RANDOM.nextInt(getRandomXZSpawnRange()), 0
                , RANDOM.nextBoolean() ? RANDOM.nextInt(getRandomXZSpawnRange()) : -RANDOM.nextInt(getRandomXZSpawnRange()));
        this.vector = destination.clone().toVector().subtract(spawn.toVector()).divide(new Vector(1000, 1000, 1000));
    }

    private Entity getClosest(){
        if(entities.size() == 0){
            return null;
        }
        Entity e = entities.get(0);
        for (Entity entity : entities) {
            if(e.getLocation().getY() > entity.getLocation().getY()){
                e = entity;
            }
        }
        return e;
    }

    protected ArmorStand createArmorStand(Location location, Material m, byte data){
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(m, 1, data));
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        entities.add(armorStand);

        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, Material.BARRIER, (byte) 0);
        fallingBlock.setGravity(false);
        fallingBlock.setPassenger(armorStand);
        fallingBlock.setInvulnerable(true);
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
                PlayerOpenCarePackageEvent event = new PlayerOpenCarePackageEvent(e.getPlayer(), this, inventoryOpener == null);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()){
                    return;
                }
                if(inventoryOpener == null){
                    if(money != 0){
                        if(VaultUtils.get().giveReward(e.getPlayer(), money)){
                            e.getPlayer().sendMessage(pl.getMessageManager().get("youReceived", new String[]{"reward"}, new String[]{money+""}));
                        }
                    }
                    e.getPlayer().openInventory(inventory);
                    inventoryOpener = e.getPlayer();
                }else{
                    if(inventoryOpener == e.getPlayer()){
                        e.getPlayer().openInventory(inventory);
                    }else{
                        e.getPlayer().sendMessage(pl.getMessageManager().get("carePackageAlreadyOpened"));
                    }
                }
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
