package fr.naruse.carepackage.carepackage;

import java.util.List;

public class Model {

   private final String name;
   private final List<BlockInfo> blockInfos;
   private final int radius;

    public Model(String name, List<BlockInfo> blockInfos, int radius){
        this.name = name;
        this.blockInfos = blockInfos;
        this.radius = radius;
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
}
