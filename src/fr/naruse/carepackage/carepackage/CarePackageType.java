package fr.naruse.carepackage.carepackage;

import fr.naruse.carepackage.carepackage.type.CarePackageSimple;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;

public enum CarePackageType {

    SIMPLE(CarePackageSimple.class),
    ;

    private Class clazz;
    CarePackageType(Class<? extends CarePackage> clazz) {
        this.clazz = clazz;
    }

    public CarePackage build(CarePackagePlugin pl, String name, Location location, Inventory inventory){
        try{
            Constructor<CarePackage> constructor = clazz.getConstructor(pl.getClass(), String.class, Location.class, Inventory.class);
            return constructor.newInstance(pl, name, location, inventory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
