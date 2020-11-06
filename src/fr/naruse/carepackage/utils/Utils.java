package fr.naruse.carepackage.utils;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.List;

public class Utils {

    public static Location getConfigLocation(CarePackagePlugin pl, String path) {
        if(!pl.getConfig().contains(path+".x")){
            return null;
        }
        double x = pl.getConfig().getDouble(path+".x");
        double y = pl.getConfig().getDouble(path+".y");
        double z = pl.getConfig().getDouble(path+".z");
        int yaw = 0;
        int pitch = 0;
        if(pl.getConfig().contains(path+".yaw")){
            yaw = pl.getConfig().getInt(path+".yaw");
            pitch = pl.getConfig().getInt(path+".pitch");
        }
        World world;
        try{
            world = Bukkit.getWorld(pl.getConfig().getString(path+".world"));
        } catch (Exception e){
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
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
