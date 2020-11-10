package fr.naruse.carepackage.utils;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class WorldEditUtils {

    public static Block[] getPointsBlock(WorldEditPlugin worldEditPlugin, Player p) {
        boolean v1_12 = true;
        try{
            worldEditPlugin.getClass().getMethod("getSelection", Player.class);
        }catch (Exception e){
            v1_12 = false;
        }
        try{
            if(v1_12){
                Object selection = worldEditPlugin.getClass().getMethod("getSelection", Player.class).invoke(worldEditPlugin, p);
                if(selection == null){
                    return null;
                }
                Object min = selection.getClass().getMethod("getNativeMinimumPoint").invoke(selection);
                Object max = selection.getClass().getMethod("getNativeMaximumPoint").invoke(selection);
                Object world = selection.getClass().getMethod("getWorld").invoke(selection);
                Block block = (Block) world.getClass().getMethod("getBlockAt", int.class, int.class, int.class)
                        .invoke(world, getBlockValue(min, "X"), getBlockValue(min, "Y"), getBlockValue(min, "Z"));
                Block block1 = (Block) world.getClass().getMethod("getBlockAt", int.class, int.class, int.class)
                        .invoke(world, getBlockValue(max, "X"), getBlockValue(max, "Y"), getBlockValue(max, "Z"));
                return new Block[]{block, block1};
            }else{
                Object session = worldEditPlugin.getClass().getMethod("getSession", Player.class).invoke(worldEditPlugin, p);
                Object region = session.getClass().getMethod("getSelection", Class.forName("com.sk89q.worldedit.world.World")).invoke(session, session.getClass().getMethod("getSelectionWorld").invoke(session));
                if(region == null){
                    return null;
                }
                Object min = region.getClass().getMethod("getMinimumPoint").invoke(region);
                Object max = region.getClass().getMethod("getMaximumPoint").invoke(region);
                Object world = region.getClass().getMethod("getWorld").invoke(region);
                world = world.getClass().getMethod("getWorld").invoke(world);
                Block block = (Block) world.getClass().getMethod("getBlockAt", int.class, int.class, int.class)
                        .invoke(world, getBlockValue(min, "X"), getBlockValue(min, "Y"), getBlockValue(min, "Z"));
                Block block1 = (Block) world.getClass().getMethod("getBlockAt", int.class, int.class, int.class)
                        .invoke(world, getBlockValue(max, "X"), getBlockValue(max, "Y"), getBlockValue(max, "Z"));
                return new Block[]{block, block1};
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static int getBlockValue(Object vector, String value) throws Exception {
        return (int) vector.getClass().getMethod("getBlock"+value).invoke(vector);
    }
}
