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

    public CarePackages(CarePackagePlugin pl) {
        this.pl = pl;
        reload();
    }

    public void reload() {
        for (CarePackage carePackage : carePackages) {
            carePackage.disable();
        }
        carePackages.clear();
        for (int i = 0; i < 99999; i++) {
            if(pl.getConfig().contains("cp."+i+".name")) {
                String name = pl.getConfig().getString("cp." + i + ".name");
                CarePackageType carePackageType;
                try{
                    carePackageType = CarePackageType.valueOf(pl.getConfig().getString("cp."+i+".type"));
                }catch (Exception e){
                    pl.getLogger().warning("can't recognize CarePackageType for Care Package '"+name+"'");
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
                        ItemStack item = ItemStackSerializer.deserialize(pl.getConfig().getString("cp."+i+".inventory."+i));
                        inventory.setItem(i, item);
                    }
                }

                CarePackage carePackage = carePackageType.build(pl, name, destination, inventory);
                carePackages.add(carePackage);
            }
        }
        for (FileConfiguration model : pl.getConfigurations().getModels()) {
            String name = model.getString("name");
            if(name == null){
                pl.getLogger().warning("[Model] File '"+model.getName()+"' badly formatted");
                continue;
            }

            Location destination = Utils.getConfigLocation(pl.getConfig(), "cp."+i+".location.destination");
            if(destination == null){
                pl.getLogger().warning("[Model] Location DESTINATION not found for Care Package '"+name+"' in model '"+model.getName()+"'");
                continue;
            }
            for (int i = 0; i < 9999999; i++) {

            }
        }
        pl.getLogger().log(Level.INFO, ""+carePackages.size()+" Care Packages found");
    }

    public List<CarePackage> getCarePackages() {
        return carePackages;
    }
}
