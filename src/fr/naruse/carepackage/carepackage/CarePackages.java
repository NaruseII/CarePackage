package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.carepackage.type.CarePackageCustom;
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

    public CarePackages(CarePackagePlugin pl) {
        this.pl = pl;
        reload();
    }

    public void reload() {
        for (CarePackage carePackage : carePackages) {
            carePackage.disable();
        }
        carePackages.clear();

        int modelCount = 0;
        for (FileConfiguration model : pl.getConfigurations().getModels()) {
            String name = model.getString("name");
            int radius = model.getInt("radius");
            if(name == null){
                pl.getLogger().warning("[Model] File '"+model.getName()+"' badly formatted");
                continue;
            }

            int id = getIdFromName(name);
            if(id == -1){
                pl.getLogger().warning("[Model] Care Package '"+name+"' not found in config file");
                return;
            }

            CarePackageType carePackageType = CarePackageType.valueOf(name.toUpperCase());
            if(carePackageType == null){
                pl.getLogger().warning("[Model] Care Package with name '"+name+"' already exists. From model '"+model.getName()+"'");
                continue;
            }

            List<BlockInfo> blockInfos = Lists.newArrayList();

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if(!model.contains(i+"")){
                    break;
                }
                BlockInfo location = Utils.getConfigBlockInfo(model, i+"");
                if(location == null){
                    pl.getLogger().warning("[Model] Block with id in config '"+i+"' not found for Care Package '"+name+"' in model '"+model.getName()+"'");
                    continue;
                }
                blockInfos.add(location);
            }

            carePackageType.registerCustomModel(name, blockInfos, radius);
            modelCount++;
        }

        for (int i = 0; i < 99999; i++) {
            if(pl.getConfig().contains("cp."+i+".name")) {
                String name = pl.getConfig().getString("cp." + i + ".name");

                CarePackageType carePackageType = CarePackageType.valueOf(pl.getConfig().getString("cp."+i+".type"));
                if(carePackageType == null){
                    pl.getLogger().warning("Can't recognize CarePackageType for Care Package '"+name+"'");
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

                CarePackage carePackage = carePackageType.build(pl, name, destination, inventory);
                if(carePackages == null){
                    pl.getLogger().warning("Can't find model for '"+name+"'");
                    continue;
                }
                carePackages.add(carePackage);
            }
        }
        pl.getLogger().log(Level.INFO, ""+modelCount+" Models found");
        pl.getLogger().log(Level.INFO, ""+carePackages.size()+" Care Packages found");
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
}
