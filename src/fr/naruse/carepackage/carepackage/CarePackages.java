package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.ItemStackSerializer;
import fr.naruse.carepackage.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

public class CarePackages {

    private CarePackagePlugin pl;
    private final List<CarePackage> carePackages = Lists.newArrayList();
    private final List<CarePackageType> badlyConfiguredModels = Lists.newArrayList();

    public CarePackages(CarePackagePlugin pl) {
        this.pl = pl;
        reload();
    }

    public void reload() {
        for (CarePackage carePackage : carePackages) {
            carePackage.disable();
        }
        carePackages.clear();
        badlyConfiguredModels.clear();

        for (FileConfiguration model : pl.getConfigurations().getModels()) {
            String name = model.getString("name");
            int radius = model.getInt("radius");
            int particleViewRadius = model.getInt("particleViewRadius", 100);
            int soundBarrierEffectRadius = model.getInt("soundBarrierEffectRadius", 50);
            double speedReducer = model.getDouble("speedReducer", 0.015);
            int randomXZSpawnRange = model.getInt("randomXZSpawnRange", 35);
            int secondBeforeRemove = model.getInt("secondBeforeRemove", 60);
            int timeBeforeBarrierEffect = model.getInt("timeBeforeBarrierEffect", 8);
            if(name == null){
                pl.getLogger().warning("[Model] File '"+model.getName()+"' badly formatted");
                continue;
            }

            CarePackageType carePackageType = CarePackageType.valueOf(name.toUpperCase());
            if(carePackageType == null){
                pl.getLogger().warning("[Model] Care PackageType with name '"+name+"' not found. From model '"+model+"'");
                continue;
            }

            List<BlockInfo> blockInfos = Lists.newArrayList();

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if(!model.contains(i+"")){
                    break;
                }
                BlockInfo location = Utils.getConfigBlockInfo(model, i+"");
                if(location == null){
                    pl.getLogger().warning("[Model] Block with id in config '"+i+"' not found in model '"+model+"'");
                    continue;
                }
                blockInfos.add(location);
            }

            ParticleInfo[] particleInfos = null;
            try{
                if(model.contains("particles")){
                    List<ParticleInfo> list = Lists.newArrayList();
                    for (String format : model.getStringList("particles")) {
                        if(format.equals("empty")){
                            break;
                        }
                        list.add(ParticleInfo.Builder.fromString(format));
                    }

                    particleInfos = list.toArray(new ParticleInfo[0]);
                }
            }catch (Exception e){
                pl.getLogger().warning("[Model] Particles for model '"+name+"' badly configured");
                badlyConfiguredModels.add(carePackageType);
                e.printStackTrace();
                continue;
            }

            carePackageType.registerCustomModel(name, blockInfos, radius, particleInfos, particleViewRadius, soundBarrierEffectRadius, speedReducer,
                    randomXZSpawnRange, secondBeforeRemove, timeBeforeBarrierEffect);
        }

        for (int i = 0; i < 99999; i++) {
            if(pl.getConfig().contains("cp."+i+".name")) {
                String name = pl.getConfig().getString("cp." + i + ".name");
                int money = pl.getConfig().getInt("cp." + i + ".money");

                CarePackageType carePackageType = CarePackageType.valueOf(pl.getConfig().getString("cp."+i+".type"));
                if(carePackageType == null){
                    pl.getLogger().warning("Can't recognize CarePackageType for Care Package '"+name+"'");
                    continue;
                }

                if(badlyConfiguredModels.contains(carePackageType)){
                    continue;
                }

                Location destination = Utils.getConfigLocation(pl.getConfig(), "cp."+i+".location.destination");
                if(destination == null){
                    pl.getLogger().warning("Location DESTINATION not found for Care Package '"+name+"'");
                    continue;
                }

                Inventory inventory = Bukkit.createInventory(null, 9*6, pl.getMessageManager().get("inventory.get.name"));
                for (int o = 0; o < 9*6; o++) {
                    if(pl.getConfig().contains("cp."+i+".inventory."+o)) {
                        ItemStack item = ItemStackSerializer.deserialize(pl.getConfig().getString("cp."+i+".inventory."+o));
                        inventory.setItem(o, item);
                    }
                }

                Schedule schedule = null;
                if(pl.getConfig().contains("cp."+i+".schedule")){
                    schedule = Schedule.Builder.fromString(pl.getConfig().getString("cp."+i+".schedule"));
                }

                CarePackage carePackage = carePackageType.build(pl, name, destination, inventory, money, schedule);
                if(carePackages == null){
                    pl.getLogger().warning("Can't find model for '"+name+"'");
                    continue;
                }
                carePackages.add(carePackage);
            }
        }
        pl.getLogger().log(Level.INFO, (CarePackageType.values().size()-1)+" Models found");
        pl.getLogger().log(Level.INFO, carePackages.size()+" Care Packages found");
    }

    private int getIdFromName(String arg) {
        for (int i = 0; i < 99999; i++) {
            if(pl.getConfig().contains("cp."+i+".name") && pl.getConfig().getString("cp."+i+".name").equals(arg)){
                return i;
            }
        }
        return -1;
    }

    public List<CarePackage> getCarePackages() {
        return carePackages;
    }

    public List<CarePackageType> getBadlyConfiguredModels() {
        return badlyConfiguredModels;
    }
}
