package fr.naruse.carepackage.utils;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.carepackage.BlockInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.NumberConversions;

import java.util.List;

public class Utils {

    public static Location getConfigLocation(FileConfiguration config, String path) {
        if(!config.contains(path+".x")){
            return null;
        }
        double x = config.getDouble(path+".x");
        double y = config.getDouble(path+".y");
        double z = config.getDouble(path+".z");
        int yaw = 0;
        int pitch = 0;
        if(config.contains(path+".yaw")){
            yaw = config.getInt(path+".yaw");
            pitch = config.getInt(path+".pitch");
        }
        World world;
        try{
            world = Bukkit.getWorld(config.getString(path+".world"));
        } catch (Exception e){
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static BlockInfo getConfigBlockInfo(FileConfiguration config, String path) {
        if(!config.contains(path+".x")){
            return null;
        }
        try{
            Material m = Material.getMaterial(config.getInt(path+".type"));
            byte data = (byte) config.getInt(path+".data");
            int x = config.getInt(path+".x");
            int y = config.getInt(path+".y");
            int z = config.getInt(path+".z");
            return new BlockInfo(m, data, x, y, z);
        }catch (Exception e){
            return null;
        }
    }

    public static List<Block> getCircle(Location center, int r){
        final List<Block> list = Lists.newArrayList();
        for(double x = -r; x <= r; x++){
            for(double z = -r; z <= r; z++){
                if((int) center.clone().add(x, 0, z).distance(center) == r){
                    list.add(center.clone().add(x, 0, z).getBlock());
                }
            }
        }
        return list;
    }

    public static double distanceXZ(Location location, Location destination) {
        return Math.sqrt(NumberConversions.square(location.getX() - destination.getX()) + NumberConversions.square(location.getY() - destination.getY()) + NumberConversions.square(location.getZ() - destination.getZ()));
    }
}
