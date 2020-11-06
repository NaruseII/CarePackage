package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.naruse.carepackage.carepackage.type.CarePackageCustom;
import fr.naruse.carepackage.carepackage.type.CarePackageSimple;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class CarePackageType {

    private static final List<CarePackageType> carePackageTypes = Lists.newArrayList();
    private static final Map<String, CarePackageType> carePackageTypeMap = Maps.newHashMap();
    private static final Map<CarePackageType, CarePackageCustom> carePackageCloneMap = Maps.newHashMap();

    public static final CarePackageType SIMPLE = new CarePackageType(CarePackageSimple.class, "SIMPLE", false);

    static {
        carePackageTypes.add(SIMPLE);
    }

    private Class clazz;
    private String name;
    private boolean isCustom = false;
    public CarePackageType(Class<? extends CarePackage> clazz, String name, boolean isCustom) {
        this.clazz = clazz;
        this.name = name;
        this.isCustom = isCustom;
    }

    public String getName(){
        return name;
    }

    public CarePackage build(CarePackagePlugin pl, String name, Location location, Inventory inventory){
        if(isCustom){
            if(carePackageCloneMap.containsKey(this)){
                return carePackageCloneMap.get(this).clone();
            }
        }
        try{
            Constructor<CarePackage> constructor = clazz.getConstructor(pl.getClass(), String.class, Location.class, Inventory.class);
            return constructor.newInstance(pl, name, location, inventory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static CarePackageType registerCarePackage(String name){
        if(carePackageTypeMap.containsKey(name)){
            return null;
        }
        CarePackageType carePackageType = new CarePackageType(CarePackageCustom.class, name, true);
        carePackageTypeMap.put(name, carePackageType);
        carePackageTypes.add(carePackageType);
        return carePackageType;
    }

    public static void clear() {
        carePackageTypes.clear();
        carePackageTypeMap.clear();
        carePackageTypes.add(SIMPLE);
    }

    public static CarePackageType valueOf(String name){
        if(carePackageTypeMap.containsKey(name)){
            return carePackageTypeMap.get(name);
        }
        return null;
    }

    public static List<CarePackageType> values(){
        return carePackageTypes;
    }
}
