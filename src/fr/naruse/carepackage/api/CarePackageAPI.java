package fr.naruse.carepackage.api;

import fr.naruse.carepackage.carepackage.*;
import fr.naruse.carepackage.carepackage.type.CarePackageCustom;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class CarePackageAPI {

    private static CarePackagePlugin CARE_PACKAGE_PLUGIN;

    public static void loadAPI(CarePackagePlugin carePackagePlugin) {
        CARE_PACKAGE_PLUGIN = carePackagePlugin;
    }

    public static CarePackagePlugin getCarePackagePlugin() {
        return CARE_PACKAGE_PLUGIN;
    }

    public CarePackage getCarePackageByName(String name){
        for (int i = 0; i < CARE_PACKAGE_PLUGIN.getCarePackages().getCarePackages().size(); i++) {
            CarePackage cp = CARE_PACKAGE_PLUGIN.getCarePackages().getCarePackages().get(i);
            if(cp.getName().equals(name)){
                return cp;
            }
        }
        return null;
    }

    public static void reload(){
        CarePackageType.clear();
        CARE_PACKAGE_PLUGIN.getConfigurations().reload();
        if(CARE_PACKAGE_PLUGIN.getSqlManager() != null){
            CARE_PACKAGE_PLUGIN.getSqlManager().loadModels(true);
        }else{
            CARE_PACKAGE_PLUGIN.getCarePackages().reload();
        }
    }

    public static void registerCarePackage(File configFile, FileConfiguration configuration){
        CARE_PACKAGE_PLUGIN.getConfigurations().registerCarePackage(configFile, configuration);
    }

    public static void registerCarePackage(String name, String json){
        if(CARE_PACKAGE_PLUGIN.getSqlManager() != null){
             CARE_PACKAGE_PLUGIN.getSqlManager().processDecode(name, json, false);
        }
    }

    public static CarePackageType registerCarePackage(String name, List<BlockInfo> blockInfos, ParticleInfo[] particleInfos, Creator creator){
        return registerCarePackage(name, CarePackageCustom.class, blockInfos, particleInfos, creator);
    }

    public static CarePackageType registerCarePackage(String name, Class<? extends CarePackageCustom> cpClass, List<BlockInfo> blockInfos, ParticleInfo[] particleInfos, Creator creator){
        CarePackageType carePackageType = CarePackageType.registerCarePackage(name, cpClass);
        if(carePackageType == null){
            CARE_PACKAGE_PLUGIN.getLogger().log(Level.SEVERE, "Can't register CarePackageType '"+name+"'");
            return null;
        }else{
            CARE_PACKAGE_PLUGIN.getLogger().log(Level.INFO, "CarePackageType '"+name+"' registered");
        }

        carePackageType.registerCustomModel(name, blockInfos, creator.radius, particleInfos, creator.particleViewRadius, creator.soundBarrierEffectRadius, creator.speedReducer,
                creator.randomXZSpawnRange, creator.secondBeforeRemove, creator.timeBeforeBarrierEffect);

        return carePackageType;
    }

    public static CarePackage buildCarePackage(CarePackageType carePackageType, String name, Location location, Inventory inventory, int money, Schedule schedule){
        CarePackage cp = carePackageType.build(CARE_PACKAGE_PLUGIN, name, location, inventory, money, schedule);
        CARE_PACKAGE_PLUGIN.getCarePackages().getCarePackages().add(cp);
        return cp;
    }


    public static class Creator{

        private final int radius;
        private final int particleViewRadius;
        private final int soundBarrierEffectRadius;
        private final double speedReducer;
        private final int randomXZSpawnRange;
        private final int secondBeforeRemove;
        private final int timeBeforeBarrierEffect;

        public Creator(int radius, int particleViewRadius, int soundBarrierEffectRadius, double speedReducer,
                       int randomXZSpawnRange, int secondBeforeRemove, int timeBeforeBarrierEffect) {
            this.radius = radius;
            this.particleViewRadius = particleViewRadius;
            this.soundBarrierEffectRadius = soundBarrierEffectRadius;
            this.speedReducer = speedReducer;
            this.randomXZSpawnRange = randomXZSpawnRange;
            this.secondBeforeRemove = secondBeforeRemove;
            this.timeBeforeBarrierEffect = timeBeforeBarrierEffect;
        }
    }
}
