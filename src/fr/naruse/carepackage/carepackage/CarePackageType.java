package fr.naruse.carepackage.carepackage;

import fr.naruse.carepackage.carepackage.type.CarePackageSimple;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Location;

import java.lang.reflect.Constructor;

public enum CarePackageType {

    SIMPLE_CARE_PACKAGE(CarePackageSimple.class);

    private Class clazz;
    CarePackageType(Class<? extends CarePackage> clazz) {
        this.clazz = clazz;
    }

    public CarePackage build(CarePackagePlugin pl, String name, Location location){
        try{
            Constructor<CarePackage> constructor = clazz.getConstructor(pl.getClass(), String.class, Location.class);
            return constructor.newInstance(pl, name, location);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
