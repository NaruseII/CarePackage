package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import org.bukkit.Location;

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

                Location destination = Utils.getConfigLocation(pl, "cp."+i+".location.destination");
                if(destination == null){
                    pl.getLogger().warning("Location DESTINATION not found for Care Package '"+name+"'");
                    continue;
                }
                CarePackage carePackage = carePackageType.build(pl, name, destination);
                carePackages.add(carePackage);
            }
        }
        pl.getLogger().log(Level.INFO, ""+carePackages.size()+" Care Packages found");
    }

    public List<CarePackage> getCarePackages() {
        return carePackages;
    }
}
